package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.QuickHotelReservation;

public class HotelReservationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8389523452874762570L;
	private Date fromDate;
	private Date toDate;
	private String hotelRoomNumber;
	private ArrayList<String> additionalServiceNames;
	private String hotelName;
	private Long quickReservationID;
	private Boolean done;
	private Double roomGrade;
	private Double hotelGrade;
	private Long id;

	public HotelReservationDTO(HotelReservation hr) {
		this.fromDate = hr.getFromDate();
		this.toDate = hr.getToDate();
		this.hotelRoomNumber = hr.getHotelRoom().getRoomNumber();
		ArrayList<String> additionalServices = new ArrayList<String>();
		hr.getAdditionalServices().forEach(h -> additionalServices.add(h.getName()));
		this.additionalServiceNames = additionalServices;
		this.hotelName = hr.getHotelRoom().getHotel().getName();
		this.quickReservationID = null;
		this.done = hr.getDone();
		this.roomGrade = hr.getGrade();
		this.hotelGrade = hr.getHotelRoom().getHotel().getAverageGrade();
		this.id = hr.getId();
	}

	public HotelReservationDTO(QuickHotelReservation hr) {
		this.fromDate = hr.getFromDate();
		this.toDate = hr.getToDate();
		this.hotelRoomNumber = hr.getHotelRoom().getRoomNumber();
		ArrayList<String> additionalServices = new ArrayList<String>();
		hr.getAdditionalServices().forEach(h -> additionalServices.add(h.getName()));
		this.additionalServiceNames = additionalServices;
		this.hotelName = hr.getHotelRoom().getHotel().getName();
		this.quickReservationID = hr.getId();
		this.done = hr.getDone();
		this.roomGrade = hr.getGrade();
		this.hotelGrade = hr.getHotelRoom().getHotel().getAverageGrade();
		this.id = hr.getId();
	}

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

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Double getRoomGrade() {
		return roomGrade;
	}

	public void setRoomGrade(Double roomGrade) {
		this.roomGrade = roomGrade;
	}

	public Double getHotelGrade() {
		return hotelGrade;
	}

	public void setHotelGrade(Double hotelGrade) {
		this.hotelGrade = hotelGrade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
