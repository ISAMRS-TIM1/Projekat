package isamrs.tim1.model;

import java.io.Serializable;
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

import isamrs.tim1.dto.HotelRoomDTO;

@Entity
@Table(name = "HotelRooms")
public class HotelRoom implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "hotelroom_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "averageGrade", unique = false, nullable = false)
	private Double averageGrade;

	@Column(name = "numberOfPeople", unique = false, nullable = false)
	private Integer numberOfPeople;

	@Column(name = "roomNumber", unique = false, nullable = false)
	private String roomNumber;

	@Column(name = "defaultPriceOneNight", unique = false, nullable = false)
	private Double defaultPriceOneNight;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotel")
	private Hotel hotel;

	@OneToMany(mappedBy = "hotelRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<SeasonalHotelRoomPrice> seasonalPrices;

	@OneToMany(mappedBy = "hotelRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickHotelReservation> quickReservations;

	@OneToMany(mappedBy = "hotelRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelReservation> normalReservations;

	public HotelRoom() {
		super();
		quickReservations = new HashSet<QuickHotelReservation>();
		normalReservations = new HashSet<HotelReservation>();
		seasonalPrices = new HashSet<SeasonalHotelRoomPrice>();
	}

	public HotelRoom(HotelRoomDTO hotelRoom, Hotel hotel) {
		this.id = null;
		this.averageGrade = 0.0;
		this.numberOfPeople = hotelRoom.getNumberOfPeople();
		this.defaultPriceOneNight = hotelRoom.getPrice();
		this.roomNumber = hotelRoom.getRoomNumber();
		this.hotel = hotel;
		this.quickReservations = new HashSet<QuickHotelReservation>();
		this.normalReservations = new HashSet<HotelReservation>();
		this.seasonalPrices = new HashSet<SeasonalHotelRoomPrice>();
	}
	public void update(HotelRoomDTO hotelRoom) {
		this.numberOfPeople = hotelRoom.getNumberOfPeople();
		this.defaultPriceOneNight = hotelRoom.getPrice();
		this.roomNumber = hotelRoom.getRoomNumber();
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

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Double getDefaultPriceOneNight() {
		return defaultPriceOneNight;
	}

	public void setDefaultPriceOneNight(Double defaultPriceOneNight) {
		this.defaultPriceOneNight = defaultPriceOneNight;
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

	public Set<SeasonalHotelRoomPrice> getSeasonalPrices() {
		return seasonalPrices;
	}

	public void setSeasonalPrices(Set<SeasonalHotelRoomPrice> seasonalPrices) {
		this.seasonalPrices = seasonalPrices;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1359942200118829407L;

}
