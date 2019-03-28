package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.RentACar;

public class RentACarDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3378278723268266095L;
	private String name;
	private String description;
	private double averageGrade;
	private double averagePrice;
	private double latitude;
	private double longitude;

	public RentACarDTO(RentACar rentACar) {
		this.name = rentACar.getName();
		this.description = rentACar.getDescription();
		this.averageGrade = rentACar.getAverageGrade();
		this.averagePrice = rentACar.getAveragePrice();
		this.latitude = rentACar.getLocation().getLatitude();
		this.longitude = rentACar.getLocation().getLongitude();
	}

	public RentACarDTO(String name, String description, double averageGrade, double averagePrice, double latitude,
			double longitude) {
		super();
		this.name = name;
		this.description = description;
		this.averageGrade = averageGrade;
		this.averagePrice = averagePrice;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
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

}
