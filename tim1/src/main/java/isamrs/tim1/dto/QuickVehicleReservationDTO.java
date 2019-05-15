package isamrs.tim1.dto;

import java.util.Date;

public class QuickVehicleReservationDTO {
	private Date fromDate;
	private Date toDate;
	private Integer vehicle;
	private Long branchOffice;
	private Integer discount;

	public QuickVehicleReservationDTO() {
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

	public Integer getVehicle() {
		return vehicle;
	}

	public void setVehicle(Integer vehicle) {
		this.vehicle = vehicle;
	}

	public Long getBranchOffice() {
		return branchOffice;
	}

	public void setBranchOffice(Long branchOffice) {
		this.branchOffice = branchOffice;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

}
