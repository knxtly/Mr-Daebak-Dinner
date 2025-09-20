package com.devak.mrdaebakdinner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {
    @NotBlank(message = "디너 종류를 선택해주세요.")
    private String dinnerKind;
    @NotBlank(message = "디너 스타일을 선택해주세요.")
    private String dinnerStyle;
    @NotBlank(message = "배달 주소를 입력해주세요.")
    private String deliveryAddress;
//    @NotNull
    private Integer totalPrice;
    @NotBlank(message = "카드번호를 입력해주세요.")
    private String cardNumber;
}
