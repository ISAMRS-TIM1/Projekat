package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "branchOffices")
public class BranchOffice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8729860394188669019L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "branchOffice_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location")
	private Location location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rentACar")
	private RentACar rentACar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public BranchOffice() {
		super();
	}

	public RentACar getRentACar() {
		return rentACar;
	}

	public void setRentACar(RentACar rentACar) {
		this.rentACar = rentACar;
	}

}
