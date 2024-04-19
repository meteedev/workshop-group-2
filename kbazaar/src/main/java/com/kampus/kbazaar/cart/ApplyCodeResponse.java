package com.kampus.kbazaar.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCodeResponse {
    private String username;
    private List<CartDetail> items;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
}
