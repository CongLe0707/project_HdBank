package com.example.Hdbank_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectReasonDTO {
    @NotBlank(message = "Reason must not be blank")
    private String reason;
}
