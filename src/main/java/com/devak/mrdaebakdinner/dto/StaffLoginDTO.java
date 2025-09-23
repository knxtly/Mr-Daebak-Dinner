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
    @NotBlank(message = "PW")
    private String password;
}
