package isamrs.tim1.model;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import isamrs.tim1.common.RequestSender;

@Entity
@Table(name = "locations")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Location implements Serializable {

	private static final long serialVersionUID = 2189094128716388349L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "location_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "lat", unique = false, nullable = false)
	private Double latitude;

	@Column(name = "lng", unique = false, nullable = false)
	private Double longitude;

	@Column(name = "country", unique = false, nullable = false)
	private String country;

	public Location() {
		super();
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.country = this.getCountryByCoordinates();
	}

	private String getCountryByCoordinates() {
		String retVal = null;
		try {
			retVal = RequestSender.getCountry(this.latitude, this.longitude);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		if (this.longitude != null)
			this.country = this.getCountryByCoordinates();
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		if (this.latitude != null)
			this.country = this.getCountryByCoordinates();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
