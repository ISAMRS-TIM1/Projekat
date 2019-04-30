package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.SeasonalHotelRoomPrice;

public class HotelRoomDetailedDTO implements Serializable {

	private String roomNumber;
	private double averageGrade;
	private double defaultPrice;
	private int numberOfPeople;
	private ArrayList<SeasonalPriceDTO> seasonalPrices;

	public HotelRoomDetailedDTO() {
		super();
	}

	public HotelRoomDetailedDTO(HotelRoom room) {
		this.roomNumber = room.getRoomNumber();
		this.averageGrade = room.getAverageGrade();
		this.numberOfPeople = room.getNumberOfPeople();
		this.defaultPrice = room.getDefaultPriceOneNight();
		this.seasonalPrices = new ArrayList<SeasonalPriceDTO>();
		for (SeasonalHotelRoomPrice sp : room.getSeasonalPrices()) {
			this.seasonalPrices.add(new SeasonalPriceDTO(sp));
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

	public double getDefaultPrice() {
		return defaultPrice;
	}

	public void setDefaultPrice(double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}

	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public ArrayList<SeasonalPriceDTO> getSeasonalPrices() {
		return seasonalPrices;
	}

	public void setSeasonalPrices(ArrayList<SeasonalPriceDTO> seasonalPrices) {
		this.seasonalPrices = seasonalPrices;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 8865167874341959288L;

}
