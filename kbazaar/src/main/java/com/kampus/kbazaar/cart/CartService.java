package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.exceptions.BadRequestException;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.promotion.Promotion;
import com.kampus.kbazaar.promotion.PromotionRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Value("${enabled.shipping.fee:false}")
    private boolean enableShippingFee;

    private final PromotionRepository promotionRepository;
    private final ShopperRepository shopperRepository;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;

    public CartService(
            PromotionRepository promotionRepository,
            ShopperRepository shopperRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            CartDetailRepository cartDetailRepository) {
        this.promotionRepository = promotionRepository;
        this.shopperRepository = shopperRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    // story_6
    public ApplyCodeResponse applyCode(String username, ApplyCodeRequest request) {
        Optional<Shopper> shopper = shopperRepository.findByUsername(username);
        if (shopper.isPresent()) {
            Optional<Promotion> promotion = promotionRepository.findByCode(request.getCode());

            if (promotion.isPresent()) {
                if (promotion.get().getProductSkus().isBlank()) {
                    return new ApplyCodeResponse();
                }

                boolean skuMatch =
                        request.getProductSkus().stream()
                                .anyMatch(x -> promotion.get().getProductSkus().contains(x));
                if (skuMatch) {
                    return new ApplyCodeResponse();
                }
                throw new BadRequestException("Cannot use discount code.");
            } else {
                throw new NotFoundException("Promotion not found");
            }
        } else {
            throw new BadRequestException("Username not found");
        }
    }

    @Transactional
    public CreateCartResponse createCart(String userName, CartRequest cartRequest) {
        CreateCartResponse response = new CreateCartResponse();
        response.setItems(new ArrayList<>());
        Optional<Shopper> shopper = shopperRepository.findByUsername(userName);

        Optional<Cart> checkShopperHaveCart = cartRepository.findByShopperId(shopper.get().getId());

        productRepository
                .findBySku(cartRequest.getSku())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (shopper.isPresent()) {
            Cart newCart = new Cart();
            if (checkShopperHaveCart.isEmpty()) {
                newCart.setShopperId(shopper.get().getId());
                newCart.setQuantity(cartRequest.getQty());
                cartRepository.save(newCart);
            }

            Optional<Product> product = productRepository.findBySku(cartRequest.getSku());
            if (product.isPresent()) {

                Optional<CartDetail> checkCartDetail =
                        cartDetailRepository.findByCartIdAndSku(
                                Long.valueOf(
                                        (checkShopperHaveCart.isPresent())
                                                ? checkShopperHaveCart.get().getId()
                                                : newCart.getId()),
                                product.get().getSku());

                if (checkCartDetail.isEmpty()) {
                    CartDetail newCartDetail = new CartDetail();
                    newCartDetail.setQuantity(cartRequest.getQty());
                    newCartDetail.setName(product.get().getName());
                    newCartDetail.setPrice(product.get().getPrice());
                    newCartDetail.setSku(product.get().getSku());
                    newCartDetail.setDiscount(BigDecimal.valueOf(0));
                    newCartDetail.setFinalPrice(product.get().getPrice());
                    if (checkShopperHaveCart.isPresent()) {
                        newCartDetail.setCartId(checkShopperHaveCart.get().getId());
                    } else {
                        newCartDetail.setCartId(newCart.getId());
                    }
                    cartDetailRepository.save(newCartDetail);
                } else {
                    checkCartDetail
                            .get()
                            .setQuantity(
                                    checkCartDetail.get().getQuantity() + cartRequest.getQty());
                    cartDetailRepository.save(checkCartDetail.get());
                }

                response.setUsername(userName);
                List<CartDetail> cartDetailList =
                        cartDetailRepository.findByCartId(
                                (checkShopperHaveCart.isPresent())
                                        ? checkShopperHaveCart.get().getId()
                                        : newCart.getId());
                BigDecimal totalPriceForReturn = BigDecimal.valueOf(0);
                BigDecimal totalDiscountForReturn = BigDecimal.valueOf(0);
                for (CartDetail item : cartDetailList) {
                    CartItem cartItem =
                            new CartItem(
                                    item.getSku(),
                                    item.getName(),
                                    item.getQuantity(),
                                    item.getPrice(),
                                    item.getDiscount(),
                                    item.getFinalPrice());
                    totalPriceForReturn = totalPriceForReturn.add(item.getFinalPrice());
                    totalDiscountForReturn = totalDiscountForReturn.add(item.getDiscount());

                    response.getItems().add(cartItem);
                }
                if (enableShippingFee) {
                    response.setFee(BigDecimal.valueOf(25));
                } else {
                    response.setFee(BigDecimal.valueOf(0));
                }

                response.setTotalPrice(totalPriceForReturn);
                response.setTotalDiscount(totalDiscountForReturn);
            }

            return response;
        } else {
            throw new NotFoundException("User not found");
        }
    }
}
