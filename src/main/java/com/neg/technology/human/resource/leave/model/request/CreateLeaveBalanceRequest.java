package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveBalanceRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    private LocalDate effectiveDate; // Integer date yerine LocalDate effectiveDate

    @NotNull
    private BigDecimal amount;
}