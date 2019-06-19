package isamrs.tim1.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ServiceDTO implements Serializable {
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String description;
	
	@Min(-90)
	@Max(90)
	private double latitude;
	
	@Min(-180)
	@Max(180)
	private double longitude;
	
	public ServiceDTO() {
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


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private static final long serialVersionUID = -5296216815042917648L;
}
