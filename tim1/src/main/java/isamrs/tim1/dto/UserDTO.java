package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.RentACarAdmin;

public class UserDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4035837120427387508L;
	private String firstName;
	private String lastName;
	private String phone;
	private String address;
	private String email;

	public UserDTO() {
		super();
	}

	public UserDTO(String firstName, String lastName, String phone, String address, String email) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.address = address;
		this.email = email;
	}

	public UserDTO(AirlineAdmin a) {
		super();
		this.firstName = a.getFirstName();
		this.lastName = a.getLastName();
		this.phone = a.getPhoneNumber();
		this.address = a.getAddress();
		this.email = a.getEmail();
	}

	public UserDTO(HotelAdmin a) {
		super();
		this.firstName = a.getFirstName();
		this.lastName = a.getLastName();
		this.phone = a.getPhoneNumber();
		this.address = a.getAddress();
		this.email = a.getEmail();
	}

	public UserDTO(RentACarAdmin a) {
		super();
		this.firstName = a.getFirstName();
		this.lastName = a.getLastName();
		this.phone = a.getPhoneNumber();
		this.address = a.getAddress();
		this.email = a.getEmail();
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
