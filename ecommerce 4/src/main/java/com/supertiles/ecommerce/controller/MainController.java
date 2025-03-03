package com.supertiles.ecommerce.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.supertiles.ecommerce.dto.Cart;
import com.supertiles.ecommerce.dto.Customer;
import com.supertiles.ecommerce.dto.Item;
import com.supertiles.ecommerce.dto.Product;
import com.supertiles.ecommerce.dto.Seller;
import com.supertiles.ecommerce.repository.CustomerRepository;
import com.supertiles.ecommerce.repository.ProductRepository;
import com.supertiles.ecommerce.service.MyService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@Autowired
	MyService service;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CustomerRepository customerRepository;

	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}

	@GetMapping("/customer-signup")
	public String customerSignup() {
		return "customer-register.html";
	}

	@PostMapping("/customer-signup")
	public String signup(@ModelAttribute Customer customer, ModelMap map) {
		return service.signup(customer, map);
	}

	@PostMapping("/customer-otp/{id}")
	public String otp(@PathVariable int id, ModelMap map, @RequestParam int otp) {
		return service.otp(id, map, otp);
	}

	@GetMapping("/customer-login")
	public String login() {
		return "customer-login.html";
	}

	@PostMapping("/customer-login")
	public String login(@RequestParam String email, @RequestParam String password, ModelMap map, HttpSession session) {
		return service.login(email, password, map, session);
	}

	@GetMapping("/seller-signup")
	public String sellerSignup() {
		return "seller-register.html";
	}

	@PostMapping("/seller-signup")
	public String signup(@ModelAttribute Seller seller, ModelMap map) {
		return service.signup(seller, map);
	}

	@PostMapping("/seller-otp/{id}")
	public String sellerOtp(@PathVariable int id, ModelMap map, @RequestParam int otp) {
		return service.sellerOtp(id, map, otp);
	}

	@GetMapping("/seller-login")
	public String sellerLogin() {
		return "seller-login.html";
	}

	@PostMapping("/seller-login")
	public String sellerLogin(@RequestParam String email, @RequestParam String password, ModelMap map,
			HttpSession session) {
		return service.sellerLogin(email, password, map, session);
	}

	@GetMapping("/add-product")
	public String addProduct(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			return "add-product.html";
		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, ModelMap map) {
		session.removeAttribute("customer");
		session.removeAttribute("seller");
		map.put("pass", "Logout Success");
		return "home.html";
	}

	@PostMapping("/add-product")
	public String addProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, ModelMap map,
			HttpSession session) throws IOException {
		if (session.getAttribute("seller") != null) {

			byte[] picture = new byte[image.getInputStream().available()];
			image.getInputStream().read(picture);

			product.setPicture(picture);

			Seller seller = (Seller) session.getAttribute("seller");

			product.setSeller(seller);

			productRepository.save(product);

			map.put("pass", "Product Added Success");
			return "seller-home.html";

		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/manage-products")
	public String manageProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Seller seller = (Seller) session.getAttribute("seller");
			List<Product> list = productRepository.findBySeller(seller);
			if (list.isEmpty()) {
				map.put("fail", "No Products Added Yet");
				return "seller-home.html";
			} else {
				map.put("list", list);
				return "products.html";
			}
		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/seller-home")
	public String loadSellerHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			return "seller-home.html";
		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/customer-home")
	public String loadCustomerHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			return "customer-home.html";
		} else {
			map.put("fail", "Invalid Session");
			return "customer-login.html";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			productRepository.deleteById(id);
			return "redirect:/manage-products";
		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Product product = productRepository.findById(id).orElse(null);
			map.put("product", product);
			return "edit-product.html";

		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@PostMapping("/edit-product")
	public String editProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, ModelMap map,
			HttpSession session) throws IOException {
		if (session.getAttribute("seller") != null) {

			byte[] picture = new byte[image.getInputStream().available()];
			image.getInputStream().read(picture);

			if (picture.length != 0)
				product.setPicture(picture);
			else
				product.setPicture(productRepository.findById(product.getId()).orElse(null).getPicture());

			Seller seller = (Seller) session.getAttribute("seller");

			product.setSeller(seller);

			productRepository.save(product);

			map.put("pass", "Product Updated Success");
			return "redirect:/manage-products";

		} else {
			map.put("fail", "Invalid Session");
			return "seller-login.html";
		}
	}

	@GetMapping("/view-products")
	public String viewProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			List<Product> list = productRepository.findAll();
			if (list.isEmpty()) {
				map.put("fail", "No Products Added Yet");
				return "customer-home.html";
			} else {
				map.put("list", list);
				return "view-products.html";
			}
		} else {
			map.put("fail", "Invalid Session");
			return "customer-login.html";
		}
	}

	@GetMapping("/add-cart/{id}")
	public String addToCart(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {

			Product product = productRepository.findById(id).orElseThrow();
			Customer customer = (Customer) session.getAttribute("customer");
			if (product.getStock() > 0) {
				Cart cart = customer.getCart();
				List<Item> items = cart.getItems();

				boolean flag = true;
				for (Item item : items) {
					if (item.getName().equals(product.getName()) && item.getBrand().equals(product.getBrand())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					Item item = new Item();
					item.setBrand(product.getBrand());
					item.setDescription(product.getDescription());
					item.setName(product.getName());
					item.setPicture(product.getPicture());
					item.setPrice(product.getPrice());
					item.setQuantity(1);
					item.setSize(product.getSize());
					items.add(item);
					customerRepository.save(customer);
					session.setAttribute("customer", customer);
					product.setStock(product.getStock() - 1);
					productRepository.save(product);

					map.put("pass", "Product Added Success");
					return "customer-home.html";
				} else {
					map.put("fail", "Product Already in Cart");
					return "customer-home.html";
				}

			} else {
				map.put("fail", "Out of Stock");
				return "customer-home.html";
			}

		} else {
			map.put("fail", "Invalid Session");
			return "customer-login.html";
		}
	}

	@GetMapping("/view-cart")
	public String viewCart(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			Customer customer = (Customer) session.getAttribute("customer");
			List<Item> items = customer.getCart().getItems();
			if (items.isEmpty()) {

				map.put("fail", "No items Found");
				return "customer-home.html";

			} else {
				double sum = items.stream().mapToDouble(i -> (i.getPrice() * i.getQuantity())).sum();
				map.put("tp", sum);
				map.put("items", items);
				return "cart.html";
			}
		} else {
			map.put("fail", "Invalid section");
			return "customer-home.html";

		}

	}

}
