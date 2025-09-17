package com.neg.technology.human.resource.leave.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestResponse {

    private Long id;

    private String employeeFirstName;
    private String employeeLastName;

    private String leaveTypeName;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal requestedDays;
    private String status;
    private String reason;

    private String approvedByFirstName;
    private String approvedByLastName;

    private LocalDateTime approvedAt;
    private String approvalNote;
    private Boolean isCancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}