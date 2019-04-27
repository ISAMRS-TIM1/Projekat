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

	private static final long serialVersionUID = 8729860394188669019L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "branchOffice_id", unique = true, nullable = false)
	private Long id;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location")
	private Location location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rentACar")
	private RentACar rentACar;

	@Column(name = "deleted", unique = false, nullable = false)
	private boolean deleted;

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
		this.deleted = false;
	}

	public RentACar getRentACar() {
		return rentACar;
	}

	public void setRentACar(RentACar rentACar) {
		this.rentACar = rentACar;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BranchOffice other = (BranchOffice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
