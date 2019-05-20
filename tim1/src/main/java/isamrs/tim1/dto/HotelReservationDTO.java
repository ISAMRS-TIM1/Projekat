package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class HotelReservationDTO implements Serializable {

	private Date fromDate;
	private Date toDate;
	private String hotelRoomNumber;
	private ArrayList<String> additionalServiceNames;
	private String hotelName;
	private Long quickReservationID;

	public HotelReservationDTO() {
		super();
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

	public Long getQuickReservationID() {
		return quickReservationID;
	}

	public void setQuickReservationID(Long quickReservationID) {
		this.quickReservationID = quickReservationID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 7626419283876074618L;
}
