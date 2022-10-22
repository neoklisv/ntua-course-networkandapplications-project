package net.app;

import java.util.List;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.transaction.annotation.Transactional;

public interface CartRepository extends JpaRepository<Cart, Long> {
	
	@Query(value = "SELECT product.id, product.name, product.brand, product.price, cart.quantity FROM product INNER JOIN cart ON cart.product_id=product.id WHERE user_id= ?1", nativeQuery = true)
    List<Cart> findByUserId(long id);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?1, ?2, 1)", nativeQuery = true)
	void addCart(long user_id, long product_id);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO cart_data (username,id,name,brand,price,quantity) select user.username, product.id, product.name, product.brand, product.price, cart.quantity from product inner join cart on cart.product_id=product.id inner join user on cart.user_id=user.id where user_id=?1", nativeQuery = true)
	void saveCart(long user_id, String tablename);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE cart SET quantity = quantity + 1 WHERE user_id = ?1 AND product_id = ?2", nativeQuery = true)
	int incCart(long user_id, long product_id);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM cart WHERE user_id=?1 and product_id=?2", nativeQuery = true)
	int delCart(long user_id, long product_id);

}
