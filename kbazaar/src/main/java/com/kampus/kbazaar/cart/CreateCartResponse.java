package com.kampus.kbazaar.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CreateCartResponse {
    private String username;
    private List<CartItem> items;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal fee = BigDecimal.valueOf(0);
}
