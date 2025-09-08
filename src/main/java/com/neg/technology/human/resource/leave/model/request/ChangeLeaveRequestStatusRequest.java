package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 50)
    private String status;

    private String approvalNote;
}
