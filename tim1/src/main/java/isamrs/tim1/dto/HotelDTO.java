package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.QuickHotelReservation;

public class HotelDTO implements Serializable {

	private String name;
	private String description;
	private double averageGrade;
	private double latitude;
	private double longitude;
	private ArrayList<HotelAdditionalServiceDTO> additionalServices;
	private ArrayList<HotelRoomDTO> rooms;
	private ArrayList<QuickHotelReservationDTO> quickReservations;

	public HotelDTO(Hotel hotel, boolean withRooms) {
		super();
		this.name = hotel.getName();
		this.description = hotel.getDescription();
		this.averageGrade = hotel.getAverageGrade();
		this.latitude = hotel.getLocation().getLatitude();
		this.longitude = hotel.getLocation().getLongitude();
		this.additionalServices = new ArrayList<HotelAdditionalServiceDTO>();
		for (HotelAdditionalService has : hotel.getAdditionalServices()) {
			if (!has.isDeleted())
				this.additionalServices.add(new HotelAdditionalServiceDTO(has));
		}
		if (withRooms) {
			this.rooms = new ArrayList<HotelRoomDTO>();
			for (HotelRoom room : hotel.getRooms()) {
				if (!room.isDeleted())
					this.rooms.add(new HotelRoomDTO(room, new Date()));
			}
		}
		this.quickReservations = new ArrayList<QuickHotelReservationDTO>();
		for(HotelReservation res : hotel.getReservations()) {
			if(res instanceof QuickHotelReservation 
					&& res.getFlightReservation() == null) { // it is not used
				this.quickReservations.add(new QuickHotelReservationDTO((QuickHotelReservation) res));
			}
		}
	}

	public HotelDTO() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public ArrayList<HotelAdditionalServiceDTO> getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(ArrayList<HotelAdditionalServiceDTO> additionalServices) {
		this.additionalServices = additionalServices;
	}

	public ArrayList<HotelRoomDTO> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<HotelRoomDTO> rooms) {
		this.rooms = rooms;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -2445924863974924664L;

}
