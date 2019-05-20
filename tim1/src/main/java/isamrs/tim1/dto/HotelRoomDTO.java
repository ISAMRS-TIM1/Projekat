package isamrs.tim1.dto;

import java.util.Date;

import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.SeasonalHotelRoomPrice;

public class HotelRoomDTO {
	private String roomNumber;
	private double averageGrade;
	private double price;
	private int numberOfPeople;

	
	public HotelRoomDTO() {
		super();
	}


	public HotelRoomDTO(HotelRoom room, Date now) {
		this.roomNumber = room.getRoomNumber();
		this.averageGrade = room.getAverageGrade();
		this.numberOfPeople = room.getNumberOfPeople();
		this.price = room.getDefaultPriceOneNight();
		for(SeasonalHotelRoomPrice sp : room.getSeasonalPrices()) {
			if( now.after(sp.getFromDate()) && now.before(sp.getToDate())) {
				this.price = sp.getPrice();
				break;
			}
		}
	}

	public String getRoomNumber() {
		return roomNumber;
	}


	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}


	public double getAverageGrade() {
		return averageGrade;
	}


	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public int getNumberOfPeople() {
		return numberOfPeople;
	}


	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}
	
	
}
