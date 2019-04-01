package isamrs.tim1.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "RegisteredUsers")
public class RegisteredUser extends User {

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "friends", joinColumns = @JoinColumn(name = "user", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend", referencedColumnName = "user_id"))
	private Set<RegisteredUser> friends;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<UserReservation> userReservations;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ServiceGrade> serviceGrades;
	
	@Column(name = "discountPoints", unique = false, nullable = false)
	private Integer discountPoints;

	public RegisteredUser() {
		friends = new HashSet<RegisteredUser>();
		userReservations = new HashSet<UserReservation>();
		serviceGrades = new HashSet<ServiceGrade>();
		discountPoints = 0;
	}

	public Integer getDiscountPoints() {
		return discountPoints;
	}

	public void setDiscountPoints(Integer discountPoints) {
		this.discountPoints = discountPoints;
	}

	public Set<RegisteredUser> getFriends() {
		return friends;
	}

	public void setFriends(Set<RegisteredUser> friends) {
		this.friends = friends;
	}

	public Set<UserReservation> getUserReservations() {
		return userReservations;
	}

	public void setUserReservations(Set<UserReservation> userReservations) {
		this.userReservations = userReservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	public Set<ServiceGrade> getServiceGrades() {
		return serviceGrades;
	}

	public void setServiceGrades(Set<ServiceGrade> serviceGrades) {
		this.serviceGrades = serviceGrades;
	}



	private static final long serialVersionUID = 4453092532257405053L;

}
