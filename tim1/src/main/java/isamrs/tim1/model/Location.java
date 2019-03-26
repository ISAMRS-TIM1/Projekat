package isamrs.tim1.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "locations")
public class Location implements Serializable {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "location_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "lat", unique = false, nullable = false)
	private double latitude;

	@Column(name = "lng", unique = false, nullable = false)
	private double longitude;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Service service;

	public Location() {
		super();
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -5137148013892279846L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
}
