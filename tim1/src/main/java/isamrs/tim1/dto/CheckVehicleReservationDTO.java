package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

public class CheckVehicleReservationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1595971551457689943L;
	@NotNull
	private Integer vehicleID;
	@NotNull
	private Date start;
	@NotNull
	private Date end;

	public Integer getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(Integer vehicleID) {
		this.vehicleID = vehicleID;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

}
