package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.common.ReservationType;

public class ReservationGradeDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1105158966875116729L;
	private Long id;
	private double grade;
	private ReservationType type;

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReservationType getType() {
		return type;
	}

	public void setType(ReservationType type) {
		this.type = type;
	}

}
