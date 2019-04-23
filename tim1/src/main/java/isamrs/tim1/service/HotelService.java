package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.HotelDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.Location;
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

	public String editHotel(Hotel hotel, String oldName) {
		Hotel hotelToEdit = hotelRepository.findOneByName(oldName);
		if (hotelToEdit == null) {
			return "Edited hotel does not exist.";
		}

		String newName = hotel.getName();
		if (serviceRepository.findOneByName(newName) != null)
			return "Name is already in use by some other service.";
		if (newName != null) {
			hotelToEdit.setName(newName);
		}

		String newDescription = hotel.getDescription();
		if (newDescription != null) {
			hotelToEdit.setDescription(newDescription);
		}

		Location newLocation = hotel.getLocation();
		if (newLocation != null) {
			hotelToEdit.getLocation().setLatitude(hotel.getLocation().getLatitude());
			hotelToEdit.getLocation().setLongitude(hotel.getLocation().getLongitude());
		}

		try {
			hotelRepository.save(hotelToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Database error.";
		}
		return null;
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

}
