package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Location;

public class BranchOfficeDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4525327696632773915L;
	private Long id;
	private String name;
	private Location location;

	public BranchOfficeDTO(BranchOffice bo) {
		this.id = bo.getId();
		this.name = bo.getName();
		this.location = bo.getLocation();
	}

	public BranchOfficeDTO() {
		super();
	}

	public BranchOfficeDTO(Long id, String name, Location location) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
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

}
