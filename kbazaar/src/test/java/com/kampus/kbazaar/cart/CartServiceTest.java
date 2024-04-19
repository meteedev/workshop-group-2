package com.kampus.kbazaar.cart;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    private CartRequest cartRequest;
    private Cart cart;
    @Mock private CartRepository cartRepository;

    @Mock private ShopperRepository shopperRepository;

    @Mock private ProductRepository productRepository;

    @Mock private CartDetailRepository cartDetailRepository;

    @InjectMocks private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRequest = new CartRequest();
        cart = new Cart();
    }

    @Test
    void testCreateCart_checkShopperHaveCartIsNotEmpty() {
        String userName = "TechNinja";

        cartRequest.setQty(1);
        cartRequest.setSku("MOBILE-APPLE-IPHONE-12-PRO");

        cart.setId(1);
        cart.setShopperId(1L);
        cart.setQuantity(cartRequest.getQty());

        Shopper shopper = new Shopper();
        shopper.setId(1L);
        shopper.setUsername(userName);
        shopper.setEmail("techninja@example.com");

        Product product = new Product();
        product.setSku(cartRequest.getSku());
        product.setId(1L);
        product.setName("Apple iPhone 12 Pro");
        product.setPrice(BigDecimal.valueOf(20990.25));
        product.setQuantity(50);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setId(1);
        cartDetail.setQuantity(cartRequest.getQty());
        cartDetail.setName(product.getName());
        cartDetail.setPrice(product.getPrice());
        cartDetail.setSku(product.getSku());
        cartDetail.setDiscount(BigDecimal.valueOf(0));
        cartDetail.setFinalPrice(product.getPrice());

        when(shopperRepository.findByUsername(userName)).thenReturn(Optional.of(shopper));
        when(cartRepository.findByShopperId(shopper.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findBySku(cartRequest.getSku())).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartId(cart.getId())).thenReturn(List.of(cartDetail));

        CreateCartResponse response = cartService.createCart(userName, cartRequest);

        verify(cartDetailRepository, times(1)).save(any(CartDetail.class));
        assertNotNull(response);
    }

    @Test
    void testCreateCart_checkShopperHaveCartIsEmpty() {
        String userName = "TechNinja";

        cartRequest.setQty(1);
        cartRequest.setSku("MOBILE-APPLE-IPHONE-12-PRO");

        Shopper shopper = new Shopper();
        shopper.setId(1L);
        shopper.setUsername(userName);
        shopper.setEmail("techninja@example.com");

        Product product = new Product();
        product.setSku(cartRequest.getSku());
        product.setId(1L);
        product.setName("Apple iPhone 12 Pro");
        product.setPrice(BigDecimal.valueOf(20990.25));
        product.setQuantity(50);

        CartDetail cartDetail = new CartDetail();
        cartDetail.setId(1);
        cartDetail.setQuantity(cartRequest.getQty());
        cartDetail.setName(product.getName());
        cartDetail.setPrice(product.getPrice());
        cartDetail.setSku(product.getSku());
        cartDetail.setDiscount(BigDecimal.valueOf(0));
        cartDetail.setFinalPrice(product.getPrice());

        when(shopperRepository.findByUsername(userName)).thenReturn(Optional.of(shopper));
        when(cartRepository.findByShopperId(shopper.getId())).thenReturn(Optional.empty());
        when(productRepository.findBySku(cartRequest.getSku())).thenReturn(Optional.of(product));
        when(cartDetailRepository.findByCartId(cart.getId())).thenReturn(List.of(cartDetail));

        CreateCartResponse response = cartService.createCart(userName, cartRequest);

        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartDetailRepository, times(1)).save(any(CartDetail.class));
        assertNotNull(response);
    }
}
