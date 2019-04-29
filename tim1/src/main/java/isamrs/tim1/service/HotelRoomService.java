package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelRoomDetailedDTO;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.repository.HotelRoomRepository;

@Service
public class HotelRoomService {

	@Autowired
	HotelRoomRepository hotelRoomRepository;
	
	public HotelRoomDetailedDTO getHotelRoom(String roomNumber, Integer hotelID) {
		return new HotelRoomDetailedDTO(hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotelID));
	}

}
