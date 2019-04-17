package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "locations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location implements Serializable {

	private static final long serialVersionUID = 2189094128716388349L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "location_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "lat", unique = false, nullable = false)
	private double latitude;

	@Column(name = "lng", unique = false, nullable = false)
	private double longitude;

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
