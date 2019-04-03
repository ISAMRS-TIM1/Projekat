package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelDTO;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.Location;
import isamrs.tim1.repository.HotelRepository;

@Service
public class HotelService {

	@Autowired
	private HotelRepository hotelRepository;

	public String addHotel(Hotel hotel) {
		if (hotelRepository.findOneByName(hotel.getName()) != null)
			return "Hotel with the same name already exists.";

		// HotelAdmin admin = new HotelAdmin(); // current user
		// admin.setHotel(hotel);
		// hotel.getAdmins().add(admin);

		hotel.setId(null); // to ensure INSERT command
		hotelRepository.save(hotel);
		return null;
	}

	public String editHotel(Hotel hotel, String oldName) {
		Hotel hotelToEdit = hotelRepository.findOneByName(oldName);
		if (hotelToEdit == null) {
			return "Edited hotel does not exist.";
		}

		String newName = hotel.getName();
		if (hotelRepository.findOneByName(newName) != null)
			return "Name is already in use by some other hotel.";
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

}
