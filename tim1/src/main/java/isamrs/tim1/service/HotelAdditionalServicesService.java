package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelAdditionalServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.QuickHotelReservation;
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
			return new MessageDTO("Hotel additional service with same name already exists",
					ToasterType.ERROR.toString());
		hotel.getAdditionalServices().add(new HotelAdditionalService(additionalService, hotel));
		hotelRepository.save(hotel);
		return new MessageDTO("Hotel additional service added successfully", ToasterType.SUCCESS.toString());
	}

	public HotelAdditionalServiceDTO getAdditionalService(String name, Hotel hotel) {
		return new HotelAdditionalServiceDTO(
				hotelAdditionalServicesRepository.findOneByNameAndHotel(name, hotel.getId()));
	}

	public MessageDTO editAdditionalService(HotelAdditionalServiceDTO additionalService, String name, Hotel hotel) {
		HotelAdditionalService has = hotelAdditionalServicesRepository.findOneByNameAndHotel(name, hotel.getId());
		if (has == null)
			return new MessageDTO("Hotel additional service with this name does not exist",
					ToasterType.ERROR.toString());
		if (checkForActiveReservations(has))
			return new MessageDTO("Hotel additional service has reservations bound to it",
					ToasterType.ERROR.toString());
		if (!additionalService.getName().equals(name)) // if room number was changed, check if new one is taken
			if (hotelAdditionalServicesRepository.findOneByNameAndHotel(additionalService.getName(),
					hotel.getId()) != null)
				return new MessageDTO("Hotel additional service with same name already exists",
						ToasterType.ERROR.toString());
		has.update(additionalService);
		hotelAdditionalServicesRepository.save(has);
		return new MessageDTO("Hotel additional service edited successfully", ToasterType.SUCCESS.toString());
	}

	public MessageDTO deleteAdditionalService(String name, Hotel hotel) {
		HotelAdditionalService has = hotelAdditionalServicesRepository.findOneByNameAndHotel(name, hotel.getId());
		if (has == null)
			return new MessageDTO("Hotel additional service with this name does not exist",
					ToasterType.ERROR.toString());
		if (checkForActiveReservations(has))
			return new MessageDTO("Hotel additional service has reservations bound to it",
					ToasterType.ERROR.toString());
		has.setDeleted(true);
		hotelAdditionalServicesRepository.save(has);
		return new MessageDTO("Hotel additional service deleted successfully", ToasterType.SUCCESS.toString());
	}

	private boolean checkForActiveReservations(HotelAdditionalService has) {
		boolean activeReservations = false;
		for (HotelReservation r : has.getNormalReservations()) {
			if (r.isDone()) {
				activeReservations = true;
				break;
			}
		}

		if (!activeReservations) {
			for (QuickHotelReservation r : has.getQuickReservations()) {
				if (r.isDone()) {
					activeReservations = true;
					break;
				}
			}
		}
		return activeReservations;
	}
}
