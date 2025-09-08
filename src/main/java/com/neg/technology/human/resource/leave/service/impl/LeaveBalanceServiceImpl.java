package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.*;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeavePolicyService leavePolicyService;

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable(() -> leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll()));
    }

    @Override
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required"));
        }
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findById(request.getId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId ve LeaveTypeId zorunlu"));
        }

        Mono<Employee> employeeMono = Mono.fromCallable(() ->
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new RuntimeException("Employee bulunamadı: " + request.getEmployeeId()))
        );

        Mono<LeaveType> leaveTypeMono = Mono.fromCallable(() ->
                leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new RuntimeException("LeaveType bulunamadı: " + request.getLeaveTypeId()))
        );

        return Mono.zip(employeeMono, leaveTypeMono)
                .flatMap(tuple -> {
                    Employee employee = tuple.getT1();
                    LeaveType leaveType = tuple.getT2();
                    String leaveName = leaveType.getName().trim();

                    int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : 0;
                    if (requestedDays <= 0) {
                        return Mono.error(new RuntimeException("Talep edilen gün sayısı 0'dan büyük olmalı"));
                    }

                    int requestYear = request.getDate() != null ? request.getDate() : LocalDate.now().getYear();

                    // max gün kuralı
                    int maxDays;
                    switch (leaveName) {
                        case "Ebeveyn İzni":
                            String gender = employee.getPerson() != null ? employee.getPerson().getGender() : null;
                            if ("female".equalsIgnoreCase(gender)) {
                                maxDays = leaveType.getDefaultDays() != null ? leaveType.getDefaultDays() : 112;
                            } else if ("male".equalsIgnoreCase(gender)) {
                                maxDays = 5;
                            } else {
                                return Mono.error(new RuntimeException("Ebeveyn izni için cinsiyet bilinmiyor"));
                            }
                            break;
                        default:
                            maxDays = leaveType.getDefaultDays() != null ? leaveType.getDefaultDays() : Integer.MAX_VALUE;
                    }

                    // tüm yıllardaki balance'ları sırayla al
                    List<LeaveBalance> balances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByDateAsc(employee.getId(), leaveType.getId());

                    // sadece request yılına ait balance
                    List<LeaveBalance> yearBalances = balances.stream()
                            .filter(b -> b.getDate().equals(requestYear))
                            .toList();

                    int usedDaysInYear = yearBalances.stream()
                            .mapToInt(LeaveBalance::getUsedDays)
                            .sum();

                    if (usedDaysInYear + requestedDays > maxDays) {
                        return Mono.error(new RuntimeException(
                                leaveName + " için izin talebi maksimum gün sayısını aşıyor (" + maxDays + ")"
                        ));
                    }

                    // mevcut yıl balance
                    LeaveBalance yearBalance = yearBalances.stream()
                            .findFirst()
                            .orElse(null);

                    // yeni balance ekleme veya mevcut balance güncelleme
                    if (yearBalance != null) {
                        yearBalance.setAmount(yearBalance.getAmount().add(BigDecimal.valueOf(requestedDays)));
                        yearBalance.setUsedDays(yearBalance.getUsedDays() + requestedDays);
                        leaveBalanceRepository.save(yearBalance);
                    } else {
                        LeaveBalance newBalance = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .date(requestYear)
                                .amount(BigDecimal.valueOf(requestedDays))
                                .usedDays(requestedDays)
                                .build();
                        leaveBalanceRepository.save(newBalance);
                        yearBalance = newBalance;
                    }

                    // response hazırla
                    LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                            .id(yearBalance.getId())
                            .employeeFirstName(employee.getPerson().getFirstName())
                            .employeeLastName(employee.getPerson().getLastName())
                            .leaveTypeName(leaveType.getName())
                            .leaveTypeBorrowableLimit(leaveType.getBorrowableLimit())
                            .leaveTypeIsUnpaid(leaveType.getIsUnpaid())
                            .date(yearBalance.getDate())
                            .amount(yearBalance.getAmount())
                            .build();

                    return Mono.just(response);
                });
    }










    @Override
    public Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required for update"));
        }

        return Mono.fromCallable(() -> {
            LeaveBalance existing = leaveBalanceRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

            Employee employee = null;
            if (request.getEmployeeId() != null) {
                employee = employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            }

            LeaveType leaveType = null;
            if (request.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));
            }

            leaveBalanceMapper.updateEntity(existing, request, employee, leaveType);
            LeaveBalance updated = leaveBalanceRepository.save(existing);

            Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(updated);
        });
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id cannot be null"));
        }

        return Mono.fromRunnable(() -> {
            if (!leaveBalanceRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getId());
            }
            leaveBalanceRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveBalance.class, request.getId());
        });
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getYear())
                )
        );
    }


    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndDate(
                                request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() + ", Year: " + request.getYear()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                )
        );
    }
}
