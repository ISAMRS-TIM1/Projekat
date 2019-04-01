package isamrs.tim1.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "HotelRooms")
public class HotelRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "service_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "averageGrade", unique = false, nullable = false)
	private Double averageGrade;

	@Column(name = "numberOfPeople", unique = false, nullable = false)
	private Integer numberOfPeople;

	@Column(name = "roomNumber", unique = false, nullable = false)
	private Integer roomNumber;

	@Column(name = "priceOneNight", unique = false, nullable = false)
	private Double priceOneNight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotel")
	private Hotel hotel;
	
	@OneToMany(mappedBy = "hotelRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickHotelReservation> quickReservations;
	
	@OneToMany(mappedBy = "hotelRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelReservation> normalReservations;
	
	public HotelRoom() {
		super();
		quickReservations = new HashSet<QuickHotelReservation>();
		normalReservations = new HashSet<HotelReservation>();
	}

	public Double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(Double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public Integer getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(Integer numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public Integer getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Double getPriceOneNight() {
		return priceOneNight;
	}

	public void setPriceOneNight(Double priceOneNight) {
		this.priceOneNight = priceOneNight;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Set<QuickHotelReservation> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(Set<QuickHotelReservation> quickReservations) {
		this.quickReservations = quickReservations;
	}

	public Set<HotelReservation> getNormalReservations() {
		return normalReservations;
	}

	public void setNormalReservations(Set<HotelReservation> normalReservations) {
		this.normalReservations = normalReservations;
	}

	
	

}
