package isamrs.tim1.dto;

import java.io.Serializable;

public class ServiceDTO implements Serializable {

	private String name;
	private String description;
	private double latitude;
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
