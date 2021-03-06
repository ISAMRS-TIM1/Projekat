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

@Entity
public class RegisteredUser extends User {

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "friends", joinColumns = @JoinColumn(name = "user", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend", referencedColumnName = "user_id"))
	private Set<RegisteredUser> friends;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "invitedUsers", joinColumns = @JoinColumn(name = "inviter", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "invited", referencedColumnName = "user_id"))
	private Set<RegisteredUser> invitedUsers;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "inviters", joinColumns = @JoinColumn(name = "invited", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "inviter", referencedColumnName = "user_id"))
	private Set<RegisteredUser> inviters;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<FlightReservation> flightReservations;

	@OneToMany(mappedBy = "invited", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<FlightInvitation> invitingReservations;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ServiceGrade> serviceGrades;

	@Column(name = "discountPoints", unique = false)
	private Integer discountPoints;

	public RegisteredUser() {
		friends = new HashSet<RegisteredUser>();
		flightReservations = new HashSet<FlightReservation>();
		serviceGrades = new HashSet<ServiceGrade>();
		invitingReservations = new HashSet<FlightInvitation>();
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

	public Set<FlightReservation> getFlightReservations() {
		return flightReservations;
	}

	public void setFlightReservations(Set<FlightReservation> flightReservations) {
		this.flightReservations = flightReservations;
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

	public Set<RegisteredUser> getInvitedUsers() {
		return invitedUsers;
	}

	public void setInvitedUsers(Set<RegisteredUser> invitedUsers) {
		this.invitedUsers = invitedUsers;
	}

	public Set<RegisteredUser> getInviters() {
		return inviters;
	}

	public void setInviters(Set<RegisteredUser> inviters) {
		this.inviters = inviters;
	}

	public Set<FlightInvitation> getInvitingReservations() {
		return invitingReservations;
	}

	public void setInvitingReservations(Set<FlightInvitation> invitingReservations) {
		this.invitingReservations = invitingReservations;
	}

	private static final long serialVersionUID = 4453092532257405053L;
}
