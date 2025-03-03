package com.supertiles.ecommerce.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Customer {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	String name;
	long mobile;
	String email;
	String password;
	int otp;
	boolean verified;

	@OneToOne(cascade = CascadeType.ALL)
	Cart cart = new Cart();
}
