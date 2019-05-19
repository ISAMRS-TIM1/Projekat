package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.Date;

public class VehicleReservationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8136623023450172437L;
	private Date fromDate;
	private Date toDate;
	private String vehicleProducer;
	private String vehicleModel;
	private String branchOfficeName;
	private Long quickVehicleReservationID;

	public VehicleReservationDTO() {
		super();
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getVehicleProducer() {
		return vehicleProducer;
	}

	public void setVehicleProducer(String vehicleProducer) {
		this.vehicleProducer = vehicleProducer;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getBranchOfficeName() {
		return branchOfficeName;
	}

	public void setBranchOfficeName(String branchOfficeName) {
		this.branchOfficeName = branchOfficeName;
	}

	public Long getQuickVehicleReservationID() {
		return quickVehicleReservationID;
	}

	public void setQuickVehicleReservationID(Long quickVehicleReservationID) {
		this.quickVehicleReservationID = quickVehicleReservationID;
	}

}
