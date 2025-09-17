package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndEffectiveDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndEffectiveDate(Long employeeId, Long leaveTypeId, LocalDate effectiveDate);

    List<LeaveBalance> findByEmployeeIdAndLeaveTypeIdOrderByEffectiveDateAsc(Long employeeId, Long leaveTypeId);

    List<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndEffectiveDateBetween(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate);

    List<LeaveBalance> findByLeaveTypeIdAndEffectiveDateBetween(Long leaveTypeId, LocalDate startDate, LocalDate endDate);

    boolean existsById(Long id);
}

