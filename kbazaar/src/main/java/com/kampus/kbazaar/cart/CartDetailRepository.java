package com.kampus.kbazaar.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {

    @Query(
            value = "select *\n" + "from cart_detail\n" + "where cart_id = :cartId and sku = :sku",
            nativeQuery = true)
    Optional<CartDetail> findByCartIdAndSku(@Param("cartId") Long cartId, @Param("sku") String sku);

    List<CartDetail> findByCartId(Integer cartId);
}
