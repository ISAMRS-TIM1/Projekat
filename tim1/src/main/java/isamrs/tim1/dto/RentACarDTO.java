package isamrs.tim1.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import isamrs.tim1.model.RentACar;

public class RentACarDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3378278723268266095L;
	@NotNull
	@NotBlank
	private String name;
	@NotNull
	@NotBlank
	private String description;
	private double averageGrade;
	@NotNull
	private double latitude;
	@NotNull
	private double longitude;
	@NotNull
	@NotBlank
	private String oldName;

	public RentACarDTO(RentACar rentACar) {
		this.name = rentACar.getName();
		this.description = rentACar.getDescription();
		this.averageGrade = rentACar.getAverageGrade();
		this.latitude = rentACar.getLocation().getLatitude();
		this.longitude = rentACar.getLocation().getLongitude();
	}

	public RentACarDTO(String name, String description, double averageGrade, double latitude,
			double longitude) {
		super();
		this.name = name;
		this.description = description;
		this.averageGrade = averageGrade;
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

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

}
