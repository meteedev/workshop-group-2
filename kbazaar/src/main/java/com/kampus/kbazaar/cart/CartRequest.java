package com.kampus.kbazaar.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartRequest {
    @NotNull private String sku;

    @Min(value = 1, message = "QTY must be greater than or equal to 0")
    private Integer qty;
}
