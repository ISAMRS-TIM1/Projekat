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

	public HotelRoomDetailedDTO getHotelRoom(String roomNumber, Hotel hotel) {
		return new HotelRoomDetailedDTO(hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotel.getId()));
	}

	public MessageDTO addHotelRoom(HotelRoomDTO hotelRoom, Hotel hotel) {
		if (hotelRoomRepository.findOneByNumberAndHotel(hotelRoom.getRoomNumber(), hotel.getId()) != null)
			return new MessageDTO("Hotel room with same number already exists", ToasterType.ERROR.toString());
		hotel.getRooms().add(new HotelRoom(hotelRoom, hotel));
		hotelRepository.save(hotel);
		return new MessageDTO("Hotel room added successfully", ToasterType.SUCCESS.toString());
	}

	public MessageDTO deleteHotelRoom(String roomNumber, Hotel hotel) {
		HotelRoom hr = hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotel.getId()); 
		if (hr == null)
			return new MessageDTO("Hotel room with this number does not exist", ToasterType.ERROR.toString());
		if(!hr.getNormalReservations().isEmpty() || !hr.getQuickReservations().isEmpty())
			return new MessageDTO("Hotel room has reservations bound to it", ToasterType.ERROR.toString());
		hotelRoomRepository.delete(hr);
		return new MessageDTO("Hotel room deleted successfully", ToasterType.SUCCESS.toString());
	}

}
