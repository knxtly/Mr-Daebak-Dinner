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
public class StaffLoginDTO {
    @NotBlank(message = "PW는 필수 요소입니다.")
    private String password;
}
