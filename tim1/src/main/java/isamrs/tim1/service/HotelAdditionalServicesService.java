package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelAdditionalServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.repository.HotelAdditionalServicesRepository;
import isamrs.tim1.repository.HotelRepository;

@Service
public class HotelAdditionalServicesService {

	@Autowired
	HotelRepository hotelRepository;
	
	@Autowired
	HotelAdditionalServicesRepository hotelAdditionalServicesRepository;
	
	public MessageDTO addAdditionalService(HotelAdditionalServiceDTO additionalService, Hotel hotel) {
		if (hotelAdditionalServicesRepository.findOneByNameAndHotel(additionalService.getName(), hotel.getId()) != null)
			return new MessageDTO("Hotel additional service with same name already exists", ToasterType.ERROR.toString());
		hotel.getAdditionalServices().add(new HotelAdditionalService(additionalService, hotel));
		hotelRepository.save(hotel);
		return new MessageDTO("Hotel additional service added successfully", ToasterType.SUCCESS.toString());
	}

}
