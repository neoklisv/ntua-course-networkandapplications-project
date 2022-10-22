package net.app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query(value = "SELECT * FROM product WHERE brand = ?1", nativeQuery = true)
    List<Product> findByBrand(String brand);

}
