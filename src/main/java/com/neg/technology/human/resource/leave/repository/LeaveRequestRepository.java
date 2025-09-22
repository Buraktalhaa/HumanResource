package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);

    // ✅ Enum parametreli
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByIsCancelledTrue();

    List<LeaveRequest> findByApprovedById(Long approverId);

    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
            Long employeeId,
            Long leaveTypeId,
            LocalDate startDate,
            LocalDate endDate
    );

    // Çakışma kontrolü
    @Query("""
        SELECT lr FROM LeaveRequest lr 
        WHERE lr.employee.id = :employeeId 
        AND lr.startDate <= :endDate 
        AND lr.endDate >= :startDate
        AND lr.isCancelled = false
    """)
    List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate);

    // ✅ Onaylanmış izinleri yıl bazlı çekmek için
    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.employee.id = :employeeId
        AND lr.status = com.neg.technology.human.resource.leave.model.enums.LeaveStatus.APPROVED
    """)
    List<LeaveRequest> findApprovedByEmployeeId(Long employeeId);
}
