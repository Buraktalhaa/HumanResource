package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveBalanceRequest {
    @NotNull
    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private LocalDate effectiveDate; // Corrected to use LocalDate for dates

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number")
    private BigDecimal amount;
}