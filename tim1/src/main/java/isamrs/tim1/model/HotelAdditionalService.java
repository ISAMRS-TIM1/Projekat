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

import isamrs.tim1.dto.HotelAdditionalServiceDTO;

@Entity
@Table(name = "HotelAdditionalServices")
public class HotelAdditionalService {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "additionalservice_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "price", unique = false, nullable = false)
	private double price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotel")
	private Hotel hotel;

	@OneToMany(mappedBy = "additionalService", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickHotelReservation> quickReservations;

	@OneToMany(mappedBy = "additionalService", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelReservation> normalReservations;
	
	@Column(name = "deleted", unique = false, nullable = false)
	private boolean deleted = false;
	
	public HotelAdditionalService() {
		super();
		quickReservations = new HashSet<QuickHotelReservation>();
		normalReservations = new HashSet<HotelReservation>();
	}

	public HotelAdditionalService(HotelAdditionalServiceDTO additionalService, Hotel hotel) {
		this.id = null;
		this.name = additionalService.getName();
		this.price = additionalService.getPrice();
		this.hotel = hotel;
		this.quickReservations = new HashSet<QuickHotelReservation>();
		this.normalReservations = new HashSet<HotelReservation>();
		this.deleted = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void update(HotelAdditionalServiceDTO additionalService) {
		this.name = additionalService.getName();
		this.price = additionalService.getPrice();
	}
	
}
