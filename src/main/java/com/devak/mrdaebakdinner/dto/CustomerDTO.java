package com.devak.mrdaebakdinner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDTO {
    private Long id; // 내부 관리용 ID

    @NotBlank(message = "로그인 ID는 필수 입력값입니다.")
    @Size(min = 4, max = 30, message = "로그인 ID는 4~30자여야 합니다.")
    private String loginId; // 외부 비즈니스용 ID

    @NotBlank(message = "PW는 필수 입력값입니다.")
    @Size(min = 4, message = "PW는 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "Name은 필수 입력값입니다.")
    @Size(max = 30, message = "Name은 30자 이하여야 합니다.")
    private String name;

    private String address;
    private String contact;
    private int orderCount;
    private String membershipLevel;
}
