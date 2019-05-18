package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import isamrs.tim1.dto.ServiceDTO;

@Entity
@Table(name = "Hotels")
public class Hotel extends Service implements Serializable {

	public Hotel() {
		additionalServices = new HashSet<HotelAdditionalService>();
		rooms = new HashSet<HotelRoom>();
		admins = new HashSet<HotelAdmin>();
		reservations = new HashSet<HotelReservation>();
	}

	public Hotel(ServiceDTO hotel) {
		super(hotel);
		additionalServices = new HashSet<HotelAdditionalService>();
		rooms = new HashSet<HotelRoom>();
		admins = new HashSet<HotelAdmin>();
		reservations = new HashSet<HotelReservation>();
	}

	@OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelAdditionalService> additionalServices;

	@OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelRoom> rooms;

	@OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelAdmin> admins;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelReservation> reservations;

	public Set<HotelAdditionalService> getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(Set<HotelAdditionalService> additionalServices) {
		this.additionalServices = additionalServices;
	}

	public Set<HotelRoom> getRooms() {
		return rooms;
	}

	public void setRooms(Set<HotelRoom> rooms) {
		this.rooms = rooms;
	}

	public Set<HotelAdmin> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<HotelAdmin> admins) {
		this.admins = admins;
	}

	public Set<HotelReservation> getReservations() {
		return reservations;
	}

	public void setReservations(Set<HotelReservation> normalReservations) {
		this.reservations = normalReservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -3964984592975561537L;
}
