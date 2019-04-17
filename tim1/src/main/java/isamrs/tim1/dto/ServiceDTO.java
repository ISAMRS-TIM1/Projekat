package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.Service;

public class ServiceDTO implements Serializable  {

	private String name;
	private double averageGrade;
	private int numberOfAdmins;
	
	
	public ServiceDTO() {
		super();
	}
	
	
	public ServiceDTO(Service service) {
		super();
		name = service.getName();
		averageGrade = service.getAverageGrade();
		if(service instanceof Airline) {
			Airline airline = (Airline) service;
			numberOfAdmins = airline.getAdmins().size();
		}
		else if(service instanceof Hotel) {
			Hotel hotel = (Hotel) service;
			numberOfAdmins = hotel.getAdmins().size();
		}
		else if(service instanceof RentACar) {
			RentACar rentacar = (RentACar) service;
			numberOfAdmins = rentacar.getAdmins().size();
		}
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAverageGrade() {
		return averageGrade;
	}
	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}
	public int getNumberOfAdmins() {
		return numberOfAdmins;
	}
	public void setNumberOfAdmins(int numberOfAdmins) {
		this.numberOfAdmins = numberOfAdmins;
	}
	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private static final long serialVersionUID = -6445030195517175587L;
}
