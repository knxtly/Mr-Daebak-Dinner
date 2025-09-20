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
    @NotBlank(message = "ID")
    private String loginId; // 외부 비즈니스용 ID
    @NotBlank(message = "PW")
    private String password;
}
