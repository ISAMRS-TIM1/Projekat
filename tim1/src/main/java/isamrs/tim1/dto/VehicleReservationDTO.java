package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.Date;

import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.VehicleReservation;

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
	private Integer discount;

	public VehicleReservationDTO() {
		super();
	}

	public VehicleReservationDTO(QuickVehicleReservation qvr) {
		this.fromDate = qvr.getFromDate();
		this.toDate = qvr.getToDate();
		this.vehicleProducer = qvr.getVehicle().getProducer();
		this.vehicleModel = qvr.getVehicle().getModel();
		this.branchOfficeName = qvr.getBranchOffice().getName();
		this.quickVehicleReservationID = qvr.getId();
		this.discount = qvr.getDiscount();
	}

	public VehicleReservationDTO(VehicleReservation vr) {
		this.fromDate = vr.getFromDate();
		this.toDate = vr.getToDate();
		this.vehicleProducer = vr.getVehicle().getProducer();
		this.vehicleModel = vr.getVehicle().getModel();
		this.branchOfficeName = vr.getBranchOffice().getName();
		this.quickVehicleReservationID = null;
		this.discount = null;
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

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

}
