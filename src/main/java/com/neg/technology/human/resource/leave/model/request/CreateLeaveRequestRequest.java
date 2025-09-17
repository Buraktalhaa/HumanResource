package com.neg.technology.human.resource.leave.model.request;

import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveRequestRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Requested days is required")
    private BigDecimal requestedDays;

    @NotNull(message = "Status is required")
    private LeaveStatus status;

    private String reason;

    @NotNull(message = "Approved by employee ID is required")
    private Long approvedById;
}