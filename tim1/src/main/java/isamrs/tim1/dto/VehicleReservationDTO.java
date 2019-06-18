package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.Date;

import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.VehicleReservation;

public class VehicleReservationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8979382260323221717L;
	private Date fromDate;
	private Date toDate;
	private String vehicleProducer;
	private String vehicleModel;
	private String branchOfficeName;
	private Long quickVehicleReservationID;
	private Integer discount;
	private double price;
	private Boolean done;
	private String rentacar;
	private Double vehicleGrade;
	private Double rentacarGrade;
	private Long id;

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
		this.price = qvr.getPrice();
		this.rentacar = qvr.getBranchOffice().getRentACar().getName();
		this.setDone(qvr.getDone());
		this.vehicleGrade = qvr.getVehicle().getAverageGrade();
		this.rentacarGrade = qvr.getVehicle().getRentACar().getAverageGrade();
		this.setId(qvr.getId());
	}

	public VehicleReservationDTO(VehicleReservation vr) {
		this.fromDate = vr.getFromDate();
		this.rentacar = vr.getBranchOffice().getRentACar().getName();
		this.toDate = vr.getToDate();
		this.vehicleProducer = vr.getVehicle().getProducer();
		this.vehicleModel = vr.getVehicle().getModel();
		this.branchOfficeName = vr.getBranchOffice().getName();
		this.quickVehicleReservationID = null;
		this.discount = null;
		this.price = vr.getPrice();
		this.done = vr.getDone();
		this.vehicleGrade = vr.getVehicle().getAverageGrade();
		this.rentacarGrade = vr.getVehicle().getRentACar().getAverageGrade();
		this.setId(vr.getId());
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getRentacar() {
		return rentacar;
	}

	public void setRentacar(String rentacar) {
		this.rentacar = rentacar;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Double getVehicleGrade() {
		return vehicleGrade;
	}

	public void setVehicleGrade(Double vehicleGrade) {
		this.vehicleGrade = vehicleGrade;
	}

	public Double getRentacarGrade() {
		return rentacarGrade;
	}

	public void setRentacarGrade(Double rentacarGrade) {
		this.rentacarGrade = rentacarGrade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
