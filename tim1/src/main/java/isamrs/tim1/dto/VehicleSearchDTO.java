package isamrs.tim1.dto;

import java.util.ArrayList;
import java.util.Date;

public class VehicleSearchDTO {
	String producer;
	ArrayList<String> models;
	ArrayList<String> vehicleTypes;
	ArrayList<String> fuelTypes;
	Integer price;
	Integer seats;
	Date startDate;
	Date endDate;
	double minGrade;
	double maxGrade;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
}
