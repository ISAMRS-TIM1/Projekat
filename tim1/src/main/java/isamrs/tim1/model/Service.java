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

	@Column(name = "description", unique = false, nullable = false)
	private String description;

	@Column(name = "averageGrade", unique = false, nullable = true)
	private Double averageGrade;

	@Column(name = "averagePrice", unique = false, nullable = true)
	private Double averagePrice;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location")
	private Location location;

	@OneToMany(mappedBy = "service", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ServiceGrade> serviceGrades;
	
	public Service() {
		super();
		serviceGrades = new HashSet<ServiceGrade>();
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

	public Double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(Double averagePrice) {
		this.averagePrice = averagePrice;
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

}
