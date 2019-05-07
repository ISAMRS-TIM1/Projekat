package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.HotelDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.Reservation;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
public class HotelService {

	@Autowired
	private HotelRepository hotelRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	public MessageDTO addHotel(Hotel hotel) {
		if (serviceRepository.findOneByName(hotel.getName()) != null)
			return new MessageDTO("Service with the same name already exists.", ToasterType.ERROR.toString());
		hotel.setId(null); // to ensure INSERT command
		hotelRepository.save(hotel);
		return new MessageDTO("Hotel successfully added", ToasterType.SUCCESS.toString());
	}

	public MessageDTO editHotel(ServiceDTO hotel, Hotel hotelToEdit) {
		String newName = hotel.getName();
		if (!newName.equals(hotelToEdit.getName())) // if hotel name was changed, check if new one is taken
			if (serviceRepository.findOneByName(newName) != null)
				return new MessageDTO("Name is already in use by some other service.", ToasterType.ERROR.toString());
		hotelToEdit.setName(newName);
		hotelToEdit.setDescription(hotel.getDescription());
		hotelToEdit.getLocation().setLatitude(hotel.getLatitude());
		hotelToEdit.getLocation().setLongitude(hotel.getLongitude());

		try {
			hotelRepository.save(hotelToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new MessageDTO("Database error.", ToasterType.ERROR.toString());
		}
		return new MessageDTO("Hotel edited successfully", ToasterType.SUCCESS.toString());
	}

	public HotelDTO getHotel(HotelAdmin admin) {
		return new HotelDTO(admin.getHotel());
	}

	public ArrayList<ServiceViewDTO> getHotels() {
		ArrayList<ServiceViewDTO> retval = new ArrayList<ServiceViewDTO>();
		for (Hotel h : hotelRepository.findAll())
			retval.add(new ServiceViewDTO(h));
		return retval;
	}

	public DetailedServiceDTO getHotel(String name) {
		return new DetailedServiceDTO(hotelRepository.findOneByName(name));
	}

	public ArrayList<ServiceViewDTO> searchHotels(String name, double fromGrade, double toGrade) {
		ArrayList<ServiceViewDTO> retval = new ArrayList<ServiceViewDTO>();
		for (Hotel h : hotelRepository.findByNameGrade(name, fromGrade, toGrade)) {
			retval.add(new ServiceViewDTO(h));
		}
		return retval;
	}

	public HotelDTO getDetailedHotel(String name) {
		return new HotelDTO(hotelRepository.findOneByName(name));
	}

	public double getIncomeOfHotel(Date fromDate, Date toDate) {
		Hotel hotel = ((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel();
		double income = 0;
		for (Reservation r : hotel.getNormalReservations()) {
			if (!(r.getDateOfReservation().before(fromDate)) && !(r.getDateOfReservation().after(toDate))
					&& r.isDone()) {
				income += r.getPrice();
			}
		}
		for (Reservation r : hotel.getQuickReservations()) {
			if (!(r.getDateOfReservation().before(fromDate)) && !(r.getDateOfReservation().after(toDate))
					&& r.isDone()) {
				income += r.getPrice();
			}
		}
		return income;
	}

}
