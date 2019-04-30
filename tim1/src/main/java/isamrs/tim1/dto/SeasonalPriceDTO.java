package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.Date;


import isamrs.tim1.model.SeasonalHotelRoomPrice;

public class SeasonalPriceDTO implements Serializable {

	private static final long serialVersionUID = -3267466116282499981L;
	private double price;
	private Date fromDate;
	private Date toDate;

	public SeasonalPriceDTO() {
		super();
	}

	public SeasonalPriceDTO(SeasonalHotelRoomPrice seasonal) {
		this.price = seasonal.getPrice();
		this.fromDate = seasonal.getFromDate();
		this.toDate = seasonal.getToDate();
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
