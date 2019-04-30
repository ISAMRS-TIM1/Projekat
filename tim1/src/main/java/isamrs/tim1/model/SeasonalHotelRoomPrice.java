package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import isamrs.tim1.dto.SeasonalPriceDTO;

@Entity
@Table(name = "SeasonalHotelRoomPrices")
public class SeasonalHotelRoomPrice implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "service_id", unique = true, nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotelRoom")
	private HotelRoom hotelRoom;

	@Column(name = "fromDate", unique = false, nullable = false)
	private Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	private Date toDate;

	@Column(name = "price", unique = false, nullable = false)
	private double price;

	public SeasonalHotelRoomPrice() {
		super();
	}

	public SeasonalHotelRoomPrice(SeasonalPriceDTO seasonalPrice, HotelRoom hr) {
		this.id = null;
		this.hotelRoom = hr;
		this.fromDate = seasonalPrice.getFromDate();
		this.toDate = seasonalPrice.getToDate();
		this.price = seasonalPrice.getPrice();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public HotelRoom getHotelRoom() {
		return hotelRoom;
	}

	public void setHotelRoom(HotelRoom hotelRoom) {
		this.hotelRoom = hotelRoom;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 3489332744613871721L;
}
