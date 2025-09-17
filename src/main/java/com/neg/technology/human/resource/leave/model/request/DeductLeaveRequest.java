package com.neg.technology.human.resource.leave.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeductLeaveRequest {
    private Long employeeId;
    private Long leaveTypeId;
    private BigDecimal amount;
    private Integer year;
}
