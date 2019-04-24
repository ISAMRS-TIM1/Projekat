package isamrs.tim1.dto;

import java.io.Serializable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleType;

public class VehicleDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9104954817019763655L;

	@NotBlank
	@NotNull
	private String model;

	@NotBlank
	@NotNull
	private String producer;

	@NotBlank
	@NotNull
	private String yearOfProduction;

	@NotNull
	@Min(1)
	private Integer numberOfSeats;

	@NotNull
	@Enumerated(EnumType.STRING)
	private FuelType fuelType;

	@NotNull
	@Enumerated(EnumType.STRING)
	private VehicleType vehicleType;

	@NotNull
	@Min(1)
	private Integer pricePerDay;

	public VehicleDTO(Vehicle v) {
		this.model = v.getModel();
		this.producer = v.getProducer();
		this.yearOfProduction = v.getYearOfProduction();
		this.numberOfSeats = v.getNumberOfSeats();
		this.fuelType = v.getFuelType();
		this.vehicleType = v.getVehicleType();
		this.pricePerDay = v.getPricePerDay();
	}

	public VehicleDTO() {
		super();
	}

	public VehicleDTO(@NotBlank @NotNull String model, @NotBlank @NotNull String producer,
			@NotBlank @NotNull String yearOfProduction, @NotBlank @NotNull Integer numberOfSeats,
			@NotBlank @NotNull FuelType fuelType, @NotBlank @NotNull VehicleType vehicleType,
			@NotBlank @NotNull Integer pricePerDay) {
		super();
		this.model = model;
		this.producer = producer;
		this.yearOfProduction = yearOfProduction;
		this.numberOfSeats = numberOfSeats;
		this.fuelType = fuelType;
		this.vehicleType = vehicleType;
		this.pricePerDay = pricePerDay;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getYearOfProduction() {
		return yearOfProduction;
	}

	public void setYearOfProduction(String yearOfProduction) {
		this.yearOfProduction = yearOfProduction;
	}

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Integer getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(Integer pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
