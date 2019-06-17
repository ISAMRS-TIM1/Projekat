package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.User;

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
	private ArrayList<FriendDTO> friends;
	private int availablePoints;

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
		this.friends = new ArrayList<FriendDTO>();
	}
	
	public UserDTO(User u) {
		this(u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getAddress(), u.getEmail());
		if (u instanceof RegisteredUser) {
			for (User us : ((RegisteredUser) u).getFriends()) {
				this.friends.add(new FriendDTO(us, "Accepted"));
			}
			for (User us : ((RegisteredUser) u).getInvitedUsers()) {
				this.friends.add(new FriendDTO(us, "Invitation sent"));
			}
			for (User us : ((RegisteredUser) u).getInviters()) {
				this.friends.add(new FriendDTO(us, "Invitation pending"));
			}
			this.availablePoints = ((RegisteredUser) u).getDiscountPoints();
		}
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

	public ArrayList<FriendDTO> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<FriendDTO> friends) {
		this.friends = friends;
	}
	

	public int getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(int availablePoints) {
		this.availablePoints = availablePoints;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
