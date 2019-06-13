package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class VehicleSearchDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6050883421806931996L;
	private String producer;
	private ArrayList<String> models;
	private ArrayList<String> vehicleTypes;
	private ArrayList<String> fuelTypes;
	private Integer price;
	private Integer seats;
	private Integer startDate;
	private Integer endDate;
	private double minGrade;
	private double maxGrade;
	private String country;

	public VehicleSearchDTO() {
		super();
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public ArrayList<String> getModels() {
		return models;
	}

	public void setModels(ArrayList<String> models) {
		this.models = models;
	}

	public ArrayList<String> getVehicleTypes() {
		return vehicleTypes;
	}

	public void setVehicleTypes(ArrayList<String> vehicleTypes) {
		this.vehicleTypes = vehicleTypes;
	}

	public ArrayList<String> getFuelTypes() {
		return fuelTypes;
	}

	public void setFuelTypes(ArrayList<String> fuelTypes) {
		this.fuelTypes = fuelTypes;
	}

	public double getMinGrade() {
		return minGrade;
	}

	public void setMinGrade(double minGrade) {
		this.minGrade = minGrade;
	}

	public double getMaxGrade() {
		return maxGrade;
	}

	public void setMaxGrade(double maxGrade) {
		this.maxGrade = maxGrade;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Integer getStartDate() {
		return startDate;
	}

	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public Integer getEndDate() {
		return endDate;
	}

	public void setEndDate(Integer endDate) {
		this.endDate = endDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
