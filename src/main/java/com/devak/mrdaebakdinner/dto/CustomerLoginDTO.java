package com.devak.mrdaebakdinner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerLoginDTO {
    @NotBlank(message = "ID는 필수 요소입니다.")
    private String loginId; // 외부 비즈니스용 ID
    @NotBlank(message = "PW는 필수 요소입니다.")
    private String password;
}
