package net.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

	@Autowired
	private CartRepository repo;
	
	public List<Cart> listAll() {
		return repo.findAll();
	}
	
	public void save(Cart product) {
		repo.save(product);
	}
	
	public Cart get(long id) {
		return repo.findById(id).get();
	}
	
	public void delete(long id) {
		repo.deleteById(id);
	}
	
    public List<Cart> findByUserId(long id) {
    	return repo.findByUserId(id);
    }
    
	public void addToCart(long user_id, long product_id) {
		
		if (repo.incCart(user_id, product_id) < 1) {
		
			repo.addCart(user_id, product_id);
		}
	}
	
	public void delCart(long user_id, long product_id) {		
			repo.delCart(user_id, product_id);
	}
	
	public void saveCart(long user_id, String name) {	
		repo.saveCart(user_id, name);
}
    
}
