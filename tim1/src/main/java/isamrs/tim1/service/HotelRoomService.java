package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelRoomDTO;
import isamrs.tim1.dto.HotelRoomDetailedDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.HotelRoomRepository;

@Service
public class HotelRoomService {

	@Autowired
	HotelRoomRepository hotelRoomRepository;

	@Autowired
	HotelRepository hotelRepository;

	public HotelRoomDetailedDTO getHotelRoom(String roomNumber, Integer hotelID) {
		return new HotelRoomDetailedDTO(hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotelID));
	}

	public MessageDTO addHotelRoom(HotelRoomDTO hotelRoom, Hotel hotel) {
		if (hotelRoomRepository.findOneByNumberAndHotel(hotelRoom.getRoomNumber(), hotel.getId()) != null)
			return new MessageDTO("Hotel room with same number already exists", ToasterType.ERROR.toString());
		hotel.getRooms().add(new HotelRoom(hotelRoom, hotel));
		hotelRepository.save(hotel);
		return new MessageDTO("Hotel room added successfully", ToasterType.SUCCESS.toString());
	}

}
