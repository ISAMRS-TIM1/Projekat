package isamrs.tim1.dto;

import java.io.Serializable;

public class ServiceGradeDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6243544075195637738L;
	private String serviceName;
	private double grade;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

}
