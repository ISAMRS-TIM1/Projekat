package isamrs.tim1.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class RegisteredUser extends User {

	@OneToMany(mappedBy = "friends", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<RegisteredUser> friends;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Reservation> reservations;

	@Column(name = "discountPoints", unique = false, nullable = false)
	private Integer discountPoints;

	public RegisteredUser() {
		friends = new HashSet<RegisteredUser>();
		reservations = new HashSet<Reservation>();
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

	public Set<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(Set<Reservation> reservations) {
		this.reservations = reservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 4453092532257405053L;

}
