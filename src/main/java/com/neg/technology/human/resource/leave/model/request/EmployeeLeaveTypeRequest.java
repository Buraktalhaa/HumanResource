package com.neg.technology.human.resource.leave.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaveTypeRequest {
    private Long employeeId;
    private Long leaveTypeId;
}
