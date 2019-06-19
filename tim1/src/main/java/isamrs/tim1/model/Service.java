package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import isamrs.tim1.dto.ServiceDTO;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Service implements Serializable {

	private static final long serialVersionUID = 1401956470554926899L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "service_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "description", unique = false, nullable = false, length = 1000)
	private String description;

	@Column(name = "averageGrade", unique = false, nullable = true)
	private Double averageGrade;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "location")
	private Location location;

	@OneToMany(mappedBy = "service", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ServiceGrade> serviceGrades = new HashSet<ServiceGrade>();

	public Service() {
		super();
		this.location = new Location();
		this.averageGrade = 0.0;
	}

	public Service(ServiceDTO serviceDTO) {
		super();
		this.location = new Location();
		serviceGrades = new HashSet<ServiceGrade>();
		this.name = serviceDTO.getName();
		this.description = serviceDTO.getDescription();
		this.location.setLatitude(serviceDTO.getLatitude());
		this.location.setLongitude(serviceDTO.getLongitude());
		this.averageGrade = 0.0;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(Double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Set<ServiceGrade> getServiceGrades() {
		return serviceGrades;
	}

	public void setServiceGrades(Set<ServiceGrade> serviceGrades) {
		this.serviceGrades = serviceGrades;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Service other = (Service) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
