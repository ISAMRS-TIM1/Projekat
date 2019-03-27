package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "Users")
@Inheritance(strategy=InheritanceType.JOINED)
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "email", unique = true, nullable = false)
	@Pattern(regexp="^(.+)@(.+)$")
	private String email;

	@Column(name = "password", unique = false, nullable = false)
	private String password;
	
	@Column(name = "firstName", unique = false, nullable = false)
	@Pattern(regexp="[A-Z][a-z]*")
	private String firstName;
	
	@Column(name = "lastName", unique = false, nullable = false)
	@Pattern(regexp="[A-Z][a-z]*")
	private String lastName;

	@Column(name = "address", unique = false, nullable = false)
	private String address;

	@Column(name = "phoneNumber", unique = false, nullable = false)
	@Pattern(regexp="\\+[0-9]{12}")
	private String phoneNumber;

	@Column(name = "userType", unique = false, nullable = false)
	private UserType userType;

	public User() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	private static final long serialVersionUID = -7894899186482431843L;

}
