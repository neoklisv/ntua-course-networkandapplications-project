package net.app;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

	@Autowired
	private ProductService service; 
	
	@Autowired
	private CartService cart; 
	
	@Autowired
	private UserService user; 
	
	@RequestMapping("/")
	public String viewRoot(Model model) {
		return "redirect:/myhomepage";
	}
	
	@RequestMapping(value = "/login")
	public String Login() {
		
		return "login";
	}
	
	@RequestMapping(value = "/login_enter", method = RequestMethod.POST)
	public String LoginEnter(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) {
		System.out.print("usr=");
		System.out.println(username);
		System.out.print("pass=");
		System.out.println(password);
		
		long id;
		
		try {
		id = user.getUserId(username, password);
		}
		
		catch(Exception e) {
			return "redirect:/newUser/" + username;
		}
		
		request.getSession().setAttribute("USER_ID", id);
		
		System.out.print("id=");
		System.out.println(id);
		return "redirect:/";
	}
	
	
	@RequestMapping("/products")
	public String viewProducts(Model model, HttpSession session) {
		List<Product> listProducts = service.listAll();
		
		float vat = 1.23f;
		String vat_code;
		
		try {
			int vat_id = (int) session.getAttribute("COUNTRY");
			float f[] = {1.23f,1.10f,1.15f,1.05f,1.18f};
			String fn[] = {"Greece","UK","Cyprus","Germany","France"};
			vat = f[vat_id];
			vat_code = fn[vat_id];
		}
		
		catch(Exception e) {
			vat = 1.23f;
			vat_code="Greece";
		}
		model.addAttribute("vat", (float)vat);
		model.addAttribute("vat_code", vat_code);
		model.addAttribute("listProducts", listProducts);
		
		return "product";
	}
	
	@RequestMapping("/basket")
	public String viewCart(Model model, HttpSession session) {
		System.out.println("DEBUG");
		
		long usr_id;
		
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			usr_id = -1;
		}
		
		if (usr_id != -1) model.addAttribute("mode", "in"); else model.addAttribute("mode", "out");
		
		float vat = 1.23f;
		String vat_code;
		
		try {
			int vat_id = (int) session.getAttribute("COUNTRY");
			float f[] = {1.23f,1.10f,1.15f,1.05f,1.18f};
			String fn[] = {"Greece","UK","Cyprus","Germany","France"};
			vat = f[vat_id];
			vat_code = fn[vat_id];
		}
		
		catch(Exception e) {
			vat = 1.23f;
			vat_code="Greece";
		}
		
		List<Cart> listProducts = cart.findByUserId(usr_id);
		float total = 0;
		
		for (int i=0; i<listProducts.size(); i++) {
			float price = listProducts.get(i).getPrice();
			long q = listProducts.get(i).getQuantity();
			total=total+price*q;
		}
		total=total*vat;
		
		int cpn=0;
		
		try {
			if ((int)session.getAttribute("COUPON") == 1) cpn =1;
		}
		
		catch(Exception e) {
		}
		
		if (cpn == 1) {
			model.addAttribute("discount", 1);
		} else {
			model.addAttribute("discount", 0);
		}

		model.addAttribute("total", total);
		model.addAttribute("vat", (float)vat);
		model.addAttribute("vat_code", vat_code);
		model.addAttribute("listProducts", listProducts);
		
		return "cart";
	}
	
	@RequestMapping("/myhomepage")
	public ModelAndView showHomePage(Model model, HttpSession session) {
		ModelAndView mav = new ModelAndView("home");
		
		long usr_id;
		
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			mav.setViewName("login");
			return mav;
		}
		
		User usr = user.get(usr_id);
		mav.addObject("user", usr);
		
		return mav;
	}
	
	@RequestMapping("/newUser")
	public String showNewUserPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "new_user";
	}
	
	@RequestMapping("/newUser/{username}")
	public String showNewUserPage2(@PathVariable(name = "username") String usrname, Model model) {
		User user = new User();
		user.setUsername(usrname);
		model.addAttribute("user", user);		
		return "new_user";
	}
	
	
	@RequestMapping(value = "/country", method = RequestMethod.POST)
	public String Country(@RequestParam("country") int country, HttpServletRequest request) {
		request.getSession().setAttribute("COUNTRY", country);
		
		return "redirect:/basket";
	}
	
	@RequestMapping(value = "/countryp", method = RequestMethod.POST)
	public String CountryP(@RequestParam("country") int country, HttpServletRequest request) {
		request.getSession().setAttribute("COUNTRY", country);		
		return "redirect:/products";
	}
	
	@RequestMapping(value = "/coupon", method = RequestMethod.POST)
	public String Coupon(@RequestParam("coupon") String coupon, HttpServletRequest request) {
		System.out.print("coupon=");
		System.out.println(coupon);
		
		if ((coupon.equals("studentdiscount"))) {
			request.getSession().setAttribute("COUPON", 1);
		}
		
		else {
			request.getSession().setAttribute("COUPON", 0);
		}
		
		return "redirect:/basket";
	}
	
	@RequestMapping("/logout")
	public String destroySession(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("product") Product product) {
		service.save(product);
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("user") User usr) {
		user.save(usr);
		
		return "redirect:/";
	}
	
	@RequestMapping("/pageupdate")
	public ModelAndView showEditUserPage(Model model, HttpSession session) {
		ModelAndView mav = new ModelAndView("edit_user");
		
		long usr_id;
		
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			mav.setViewName("login");
			return mav;
		}
		
		User usr = user.get(usr_id);
		mav.addObject("user", usr);
		
		return mav;
	}
	

	@RequestMapping("/filesave")
	public String fileSave(Model model, HttpSession session) {
		
		
		long usr_id;
		
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			return "redirect:/basket";
		}
		
		User usr = user.get(usr_id);
		
		String username = user.get(usr_id).getUsername();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
		LocalDateTime now = LocalDateTime.now();  
		
		String filename = "cart_usr_" + username + "_" + dtf.format(now).toString();
		
		cart.saveCart(usr_id, filename);

		List<Cart> listProducts = cart.findByUserId(usr_id);
		
		try {
		      FileWriter myWriter = new FileWriter(filename + ".csv");
		      myWriter.write("id,name,brand,price,quantity\n");
		      for (int i=0; i<listProducts.size(); i++) {
		    	  myWriter.write(String.valueOf(listProducts.get(i).getId()));
		    	  myWriter.write(",");
		    	  myWriter.write(String.valueOf(listProducts.get(i).getName()));
		    	  myWriter.write(",");
		    	  myWriter.write(String.valueOf(listProducts.get(i).getBrand()));
		    	  myWriter.write(",");
		    	  myWriter.write(String.valueOf(listProducts.get(i).getPrice()));
		    	  myWriter.write(",");
		    	  myWriter.write(String.valueOf(listProducts.get(i).getQuantity()));
		    	  myWriter.write("\n");
		      }
		      
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	
		return "redirect:/basket";
	}
		
	
	@RequestMapping("/delCart/{id}")
	public String deleteCart(@PathVariable(name = "id") int product_id, HttpSession session) {
		long usr_id;
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			return "redirect:/login";	
		}
		
		System.out.print("usr_id=");
		System.out.println(usr_id);
		System.out.print("product_id=");
		System.out.println(product_id);
		
		cart.delCart(usr_id, product_id);
		return "redirect:/basket";		
	}
	
	@RequestMapping("/addCart/{id}")
	public String addProductCart(@PathVariable(name = "id") int product_id, HttpSession session) {
		long usr_id;
		try {
			usr_id = (long) session.getAttribute("USER_ID");
		}
		
		catch(Exception e) {
			return "redirect:/login";	
		}
		
		System.out.print("usr_id=");
		System.out.println(usr_id);
		System.out.print("product_id=");
		System.out.println(product_id);
		
		cart.addToCart(usr_id, product_id);
		return "redirect:/products";		
	}
}
