package com.devak.mrdaebakdinner.dto;

import com.devak.mrdaebakdinner.entity.OrderDnrKind;
import com.devak.mrdaebakdinner.entity.OrderDnrStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDTO {
    @NotNull(message = "디너 종류를 선택해주세요.")
    private OrderDnrKind dinnerKind;
    @NotNull(message = "디너 스타일을 선택해주세요.")
    private OrderDnrStyle dinnerStyle;
    @NotBlank(message = "배달 주소를 입력해주세요.")
    private String deliveryAddress;
    @NotBlank(message = "카드번호를 입력해주세요.")
    private String cardNumber;
}
