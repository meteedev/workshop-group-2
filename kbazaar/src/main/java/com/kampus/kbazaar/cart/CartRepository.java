package com.kampus.kbazaar.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    String sqlCart =
            "SELECT "
                    + "c.id AS cart_id, "
                    + "p.name AS product_name, "
                    + "s.username AS user_name "
                    + "FROM "
                    + "cart c "
                    + "INNER JOIN product p ON c.productid = p.id "
                    + "INNER JOIN shopper s ON c.shopperid = s.id "
                    + "WHERE "
                    + "c.shopperid = :shopperId";

    @Query(value = sqlCart, nativeQuery = true)
    public List<Object[]> findCartDetailById(@Param("shopperId") Long shopperId);

    @Query(
            value = "select *\n" + "from cart\n" + "where cart.shopperid = :shopperId",
            nativeQuery = true)
    Optional<Cart> findByShopperId(@Param("shopperId") Long shopperId);
}
