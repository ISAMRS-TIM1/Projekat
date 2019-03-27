package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.repository.HotelRepository;

@Service
public class HotelService {
	
	@Autowired
	private HotelRepository hotelRepository;

	public Boolean addHotel(Hotel hotel) {
		if(hotelRepository.findOneByName(hotel.getName()) != null) return false;
		
//		HotelAdmin admin = new HotelAdmin(); // current user
//		admin.setHotel(hotel);
//		hotel.getAdmins().add(admin);
		
		hotel.setId(null); // to ensure INSERT command
		hotelRepository.save(hotel);
		return true;
	}

}
