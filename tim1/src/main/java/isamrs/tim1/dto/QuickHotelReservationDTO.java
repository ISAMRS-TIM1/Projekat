package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.model.QuickHotelReservation;
import isamrs.tim1.service.HotelReservationService;

public class QuickHotelReservationDTO implements Serializable {

	private Date fromDate;
	private Date toDate;
	private String hotelRoomNumber;
	private ArrayList<String> additionalServiceNames;
	private String hotelName;
	private double discount;
	private double discountedPrice;

	public QuickHotelReservationDTO() {
		super();
	}

	public QuickHotelReservationDTO(QuickHotelReservation res) {
		this.fromDate = res.getFromDate();
		this.toDate = res.getToDate();
		this.hotelRoomNumber = res.getHotelRoom().getRoomNumber();
		this.additionalServiceNames = new ArrayList<String>();
		for(HotelAdditionalService has : res.getAdditionalServices()) {
			this.additionalServiceNames.add(has.getName());
		}
		this.hotelName = res.getHotelRoom().getHotel().getName();
		this.discount = res.getDiscount();
		this.discountedPrice = (this.discount/100) * HotelReservationService.calculateReservationPrice(res);
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

	public String getHotelRoomNumber() {
		return hotelRoomNumber;
	}

	public void setHotelRoomNumber(String hotelRoomNumber) {
		this.hotelRoomNumber = hotelRoomNumber;
	}

	public ArrayList<String> getAdditionalServiceNames() {
		return additionalServiceNames;
	}

	public void setAdditionalServiceNames(ArrayList<String> additionalServiceNames) {
		this.additionalServiceNames = additionalServiceNames;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -1329290622274758505L;
}
