package com.supertiles.ecommerce.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.supertiles.ecommerce.dto.Customer;
import com.supertiles.ecommerce.dto.Seller;
import com.supertiles.ecommerce.repository.CustomerRepository;
import com.supertiles.ecommerce.repository.SellerRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class MyService {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	SellerRepository sellerRepository;

	@Autowired
	JavaMailSender mailSender;

	public String signup(Customer customer, ModelMap map) {
		if (customerRepository.existsByEmail(customer.getEmail())) {
			map.put("fail", "Email Already Exists");
			return "customer-register.html";
		} else if (customerRepository.existsByMobile(customer.getMobile())) {
			map.put("fail", "Mobile Number Already Exists");
			return "customer-register.html";
		} else {
			int otp = new Random().nextInt(100000, 1000000);
			customer.setOtp(otp);
			customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
			customerRepository.save(customer);
			// sendEmail(customer.getEmail(), customer.getName(), otp);
			map.put("pass", "Otp Sent Success");
			map.put("id", customer.getId());
			return "customer-otp.html";
		}
	}

	public void sendEmail(String email, String name, int otp) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(email);
			helper.setSubject("Email Verification with SuperTiles");
			helper.setFrom("saishkulkarni7@gmail.com", "SuperTiles");
			helper.setText("<h1>Hello " + name + " Your OTP is : " + otp + "</h1>", true);
		} catch (Exception e) {
		}
		mailSender.send(message);
	}

	public String otp(int id, ModelMap map, int otp) {
		Customer customer = customerRepository.findById(id).orElseThrow();
		if (customer.getOtp() == otp) {
			customer.setVerified(true);
			customerRepository.save(customer);
			map.put("pass", "Account Created Success");
			return "home.html";
		} else {
			map.put("fail", "Invalid OTP");
			map.put("id", customer.getId());
			return "customer-otp.html";
		}
	}

	public String login(String email, String password, ModelMap map, HttpSession session) {
		Customer customer = customerRepository.findByEmail(email);
		if (customer == null) {
			map.put("fail", "Incorrect Email");
			return "customer-login.html";
		} else {
			if (password.equals(AES.decrypt(customer.getPassword(), "123"))) {
				if (customer.isVerified()) {
					session.setAttribute("customer", customer);
					map.put("pass", "Login Success");
					return "customer-home.html";
				}
				else {
					int otp = new Random().nextInt(100000, 1000000);
					customer.setOtp(otp);
					customerRepository.save(customer);
					// sendEmail(customer.getEmail(), customer.getName(), otp);
					map.put("pass", "Otp Sent Success");
					map.put("id", customer.getId());
					return "customer-otp.html";
				}
			} else {
				map.put("fail", "Incorrect Password");
				return "customer-login.html";
			}
		}
	}

	public String signup(Seller seller, ModelMap map) {
		if (sellerRepository.existsByEmail(seller.getEmail())) {
			map.put("fail", "Email Already Exists");
			return "seller-register.html";
		} else if (sellerRepository.existsByMobile(seller.getMobile())) {
			map.put("fail", "Mobile Number Already Exists");
			return "seller-register.html";
		} else {
			int otp = new Random().nextInt(100000, 1000000);
			seller.setOtp(otp);
			seller.setPassword(AES.encrypt(seller.getPassword(), "123"));
			sellerRepository.save(seller);
			// sendEmail(seller.getEmail(), seller.getName(), otp);
			map.put("pass", "Otp Sent Success");
			map.put("id", seller.getId());
			return "seller-otp.html";
		}
	}

	public String sellerOtp(int id, ModelMap map, int otp) {
		Seller seller = sellerRepository.findById(id).orElseThrow();
		if (seller.getOtp() == otp) {
			seller.setVerified(true);
			sellerRepository.save(seller);
			map.put("pass", "Account Created Success");
			return "home.html";
		} else {
			map.put("fail", "Invalid OTP");
			map.put("id", seller.getId());
			return "seller-otp.html";
		}
	}

	public String sellerLogin(String email, String password, ModelMap map, HttpSession session) {
		Seller seller = sellerRepository.findByEmail(email);
		if (seller == null) {
			map.put("fail", "Incorrect Email");
			return "seller-login.html";
		} else {
			if (password.equals(AES.decrypt(seller.getPassword(), "123"))) {
				if (seller.isVerified()) {
					map.put("pass", "Login Success");
					session.setAttribute("seller", seller);
					return "seller-home.html";
				}
				else {
					int otp = new Random().nextInt(100000, 1000000);
					seller.setOtp(otp);
					sellerRepository.save(seller);
					// sendEmail(seller.getEmail(), seller.getName(), otp);
					map.put("pass", "Otp Sent Success");
					map.put("id", seller.getId());
					return "seller-otp.html";
				}
			} else {
				map.put("fail", "Incorrect Password");
				return "seller-login.html";
			}
		}
	}
}
