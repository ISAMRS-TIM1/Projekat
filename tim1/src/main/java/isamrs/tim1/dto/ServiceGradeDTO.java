package isamrs.tim1.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ServiceGradeDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6243544075195637738L;
	@NotNull
	@NotBlank
	private String serviceName;
	@NotNull
	@Min(0)
	@Max(5)
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
