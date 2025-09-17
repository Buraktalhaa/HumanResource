package com.neg.technology.human.resource.leave.model.request;

import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLeaveRequestStatusRequest {
    @NotNull(message = "Leave request ID is required")
    private Long leaveRequestId;

    @NotNull(message = "New status is required")
    private LeaveStatus status; // String yerine enum

    private String approvalNote;
}

