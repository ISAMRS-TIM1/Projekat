package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Service;

public class DetailedServiceDTO implements Serializable  {

	private String name;
	private String description;
	private double averageGrade;
	private double latitude;
	private double longitude;
	private ArrayList<UserDTO> admins;
	
	
	public DetailedServiceDTO() {
		super();
	}
	
	
	public DetailedServiceDTO(Service service) {
		super();
		name = service.getName();
		description = service.getDescription();
		averageGrade = service.getAverageGrade();
		latitude = service.getLocation().getLatitude();
		longitude = service.getLocation().getLongitude();
		admins = new ArrayList<UserDTO>();
		if(service instanceof Airline) {
			Airline airline = (Airline) service;
			for(AirlineAdmin a : airline.getAdmins()) {
				admins.add(new UserDTO(a));
			}
		}
		else if(service instanceof Hotel) {
			Hotel hotel = (Hotel) service;
			for(HotelAdmin a : hotel.getAdmins()) {
				admins.add(new UserDTO(a));
			}
		}
		else if(service instanceof RentACar) {
			RentACar rentacar = (RentACar) service;
			for(RentACarAdmin a : rentacar.getAdmins()) {
				admins.add(new UserDTO(a));
			}
		}
	}



	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public double getAverageGrade() {
		return averageGrade;
	}


	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public ArrayList<UserDTO> getAdmins() {
		return admins;
	}


	public void setAdmins(ArrayList<UserDTO> admins) {
		this.admins = admins;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private static final long serialVersionUID = -6445030195517175587L;
}
