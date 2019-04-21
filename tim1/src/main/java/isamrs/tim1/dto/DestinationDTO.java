package isamrs.tim1.dto;

import java.io.Serializable;

public class DestinationDTO implements Serializable {
	
	private String nameOfDest;
	private Double latitude;
	private Double longitude;
	private String airlineName;
	
	public DestinationDTO() {
		super();
	}
	
	public String getNameOfDest() {
		return nameOfDest;
	}

	public void setNameOfDest(String nameOfDest) {
		this.nameOfDest = nameOfDest;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 231712408553877022L;
}
