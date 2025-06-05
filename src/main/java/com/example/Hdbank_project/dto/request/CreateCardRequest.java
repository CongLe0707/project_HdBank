package com.example.Hdbank_project.dto.request;

import com.example.Hdbank_project.model.Enum.CardType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "CMND/CCCD không được để trống")
    @Size(min = 9, max = 12, message = "CMND/CCCD phải từ 9 đến 12 ký tự")
    private String idNumber;

    @NotNull(message = "Loại thẻ không được để trống")
    private CardType cardType;

    @NotNull(message = "Ngày cấp không được để trống")
    private LocalDate issuedDate;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;

    private String notes;
}
