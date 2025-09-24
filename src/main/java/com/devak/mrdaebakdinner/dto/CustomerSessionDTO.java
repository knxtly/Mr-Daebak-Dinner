package com.devak.mrdaebakdinner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerSessionDTO {
    private String loginId;
    private String name;
    private String membership;
}
