package isamrs.tim1.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Location;

public class BranchOfficeDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4525327696632773915L;
	private Long id;
	@NotNull
	private String name;
	@NotNull
	private Location location;
	private boolean deleted;

	public BranchOfficeDTO(BranchOffice bo) {
		this.id = bo.getId();
		this.name = bo.getName();
		this.location = bo.getLocation();
		this.setDeleted(bo.isDeleted());
	}

	public BranchOfficeDTO() {
		super();
	}

	public BranchOfficeDTO(Long id, String name, Location location, boolean deleted) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.setDeleted(deleted);
	}

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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
