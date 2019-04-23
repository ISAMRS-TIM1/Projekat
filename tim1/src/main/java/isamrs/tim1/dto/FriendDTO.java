package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.User;

public class FriendDTO implements Serializable {

	private String firstName;
	private String lastName;
	private String email;
	private String status;
	
	public FriendDTO() {
		super();
	}
	
	public FriendDTO(String firstName, String lastName, String email, String status) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.status = status;
	}
	
	public FriendDTO(User u, String status) {
		super();
		this.firstName = u.getFirstName();
		this.lastName = u.getLastName();
		this.email = u.getLastName();
		this.status = status;
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


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private static final long serialVersionUID = -7174050367730030049L;

}
