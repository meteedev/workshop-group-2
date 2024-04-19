package com.kampus.kbazaar.cart;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.security.JwtAuthFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = CartController.class,
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthFilter.class))
public class CartControllerTest {

    @Autowired private MockMvc mockMvc;

    @InjectMocks private CartController cartController;

    @MockBean private CartService cartService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        //        this.mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    public void getCart_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/carts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createCart_ReturnsCreated() throws Exception {

        CartRequest cartRequest = new CartRequest();
        cartRequest.setSku("MOBILE-APPLE-IPHONE-12-PRO");
        cartRequest.setQty(1);
        String username = "TechNinja";

        CreateCartResponse createCartResponse = new CreateCartResponse();
        createCartResponse.setUsername(username);
        createCartResponse.setFee(BigDecimal.valueOf(0));
        createCartResponse.setTotalDiscount(BigDecimal.valueOf(0));
        createCartResponse.setTotalPrice(BigDecimal.valueOf(0));
        CartItem cartItem = new CartItem();
        cartItem.setSku("MOBILE-APPLE-IPHONE-12-PRO");
        cartItem.setName("Apple iPhone 12 Pro");
        cartItem.setDiscount(BigDecimal.valueOf(0));
        cartItem.setPrice(BigDecimal.valueOf(20990.25));
        cartItem.setFinalPrice(BigDecimal.valueOf(20990.25));
        ArrayList<CartItem> cartList = new ArrayList<CartItem>();
        cartList.add(cartItem);
        createCartResponse.setItems(cartList);

        when(cartService.createCart(username, cartRequest)).thenReturn(createCartResponse);
        mockMvc.perform(
                        post("/api/v1/carts/{username}/items", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"sku\":\"MOBILE-APPLE-IPHONE-12-PRO\",\"qty\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void createCart_ReturnsFailedUserNotFound() throws Exception {

        CartRequest cartRequest = new CartRequest();
        cartRequest.setSku("MOBILE-APPLE-IPHONE-12-PRO");
        cartRequest.setQty(1);
        String username = "TechNinja1";

        Exception exception = new NotFoundException("User not found");

        when(cartService.createCart(username, cartRequest)).thenThrow(exception);
        mockMvc.perform(
                        post("/api/v1/carts/{username}/items", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"sku\":\"MOBILE-APPLE-IPHONE-12-PRO\",\"qty\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCart_ReturnsFailedProductNotFound() throws Exception {

        CartRequest cartRequest = new CartRequest();
        cartRequest.setSku("MOBILE-APPLE-IPHONE-12-PRO-MAXMAX");
        cartRequest.setQty(1);
        String username = "TechNinja";

        Exception exception = new NotFoundException("Product not found");

        when(cartService.createCart(username, cartRequest)).thenThrow(exception);
        mockMvc.perform(
                        post("/api/v1/carts/{username}/items", username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"sku\":\"MOBILE-APPLE-IPHONE-12-PRO-MAXMAX\",\"qty\":1}"))
                .andExpect(status().isNotFound());
    }

    //    @Test
    //    public void applyCodeIfSuccess_ShouldReturnOk() throws Exception {
    //        String username = "TechNinja";
    //        when(cartService.applyCode(anyString(), any())).thenReturn(new ApplyCodeResponse());
    //        mockMvc.perform(post("/api/v1/carts/{username}/promotions",
    // username).contentType(MediaType.APPLICATION_JSON))
    //                .andExpect(status().isOk());
    //    }

}
