package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import isamrs.tim1.dto.HotelReservationDTO;

@Entity
@Table(name = "HotelReservations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class HotelReservation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	protected Long id;

	@Column(name = "fromDate", unique = false, nullable = false)
	protected Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	protected Date toDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotelRoom")
	protected HotelRoom hotelRoom;

	@Column(name = "done", unique = false, nullable = false)
	protected Boolean done = false;

	@Column(name = "grade", unique = false, nullable = true)
	protected Double grade = null;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "reservation_additionalservices", joinColumns = @JoinColumn(name = "reservation", referencedColumnName = "reservation_id"), inverseJoinColumns = @JoinColumn(name = "additionalservice", referencedColumnName = "additionalservice_id"))
	protected Set<HotelAdditionalService> additionalServices;

	@OneToOne(mappedBy = "hotelReservation", fetch = FetchType.LAZY)
	protected FlightReservation flightReservation;

	@Column(name = "price", unique = false, nullable = true)
	protected Double price;

	@Version
	private Integer version;

	public HotelReservation() {
		super();
		additionalServices = new HashSet<HotelAdditionalService>();
	}

	public HotelReservation(HotelReservationDTO hotelRes, HotelRoom room,
			HashSet<HotelAdditionalService> additionalServices, FlightReservation fr) {
		super();
		this.id = null;
		this.fromDate = hotelRes.getFromDate();
		this.toDate = hotelRes.getToDate();
		this.hotelRoom = room;
		this.additionalServices = additionalServices;
		this.flightReservation = fr;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public HotelRoom getHotelRoom() {
		return hotelRoom;
	}

	public void setHotelRoom(HotelRoom hotelRoom) {
		this.hotelRoom = hotelRoom;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<HotelAdditionalService> getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(Set<HotelAdditionalService> additionalServices) {
		this.additionalServices = additionalServices;
	}

	public FlightReservation getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	private static final long serialVersionUID = 2735432254411939871L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

}
