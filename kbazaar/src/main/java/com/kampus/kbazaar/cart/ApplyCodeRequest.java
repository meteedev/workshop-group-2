package com.kampus.kbazaar.cart;

import java.util.List;
import lombok.Data;

@Data
public class ApplyCodeRequest {
    private String code;
    private List<String> productSkus;
}
