package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.HotelRoomDTO;
import isamrs.tim1.dto.HotelRoomDetailedDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.SeasonalPriceDTO;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.QuickHotelReservation;
import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.SeasonalHotelRoomPrice;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.HotelRoomRepository;
import isamrs.tim1.repository.SeasonalHotelRoomPriceRepository;

@Service
public class HotelRoomService {

	@Autowired
	HotelRoomRepository hotelRoomRepository;

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	SeasonalHotelRoomPriceRepository seasonalHotelRoomPriceRepository;

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
		if (checkForActiveReservations(hr))
			return new MessageDTO("Hotel room has reservations bound to it", ToasterType.ERROR.toString());
		hr.setDeleted(true);
		hotelRoomRepository.save(hr);
		return new MessageDTO("Hotel room deleted successfully", ToasterType.SUCCESS.toString());
	}

	public MessageDTO addSeasonalPrice(SeasonalPriceDTO seasonalPrice, String roomNumber, Hotel hotel) {
		if (!seasonalPrice.getToDate().after(seasonalPrice.getFromDate())) {
			return new MessageDTO("'To' date must be after 'From' date", ToasterType.ERROR.toString());
		}

		if (seasonalPrice.getFromDate().before(new Date())) {
			return new MessageDTO("Cannot change prices in past", ToasterType.ERROR.toString());
		}

		HotelRoom hr = hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotel.getId());
		if (hr == null)
			return new MessageDTO("Hotel room with this number does not exist", ToasterType.ERROR.toString());

		// check for overlapping seasonal prices
		for (SeasonalHotelRoomPrice sp : hr.getSeasonalPrices()) {
			// (StartA < EndB) and (EndA > StartB)
			if (sp.getFromDate().before(seasonalPrice.getToDate())
					&& sp.getToDate().after(seasonalPrice.getFromDate())) {
				return new MessageDTO("These dates overlap with existing seasonal prices",
						ToasterType.ERROR.toString());
			}
		}

		hr.getSeasonalPrices().add(new SeasonalHotelRoomPrice(seasonalPrice, hr));
		hotelRoomRepository.save(hr);
		return new MessageDTO("Seasonal price added successfully", ToasterType.SUCCESS.toString());
	}

	public MessageDTO deleteSeasonalPrice(SeasonalPriceDTO seasonalPrice, String roomNumber, Hotel hotel) {
		HotelRoom hr = hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotel.getId());
		if (hr == null)
			return new MessageDTO("Hotel room with this number does not exist", ToasterType.ERROR.toString());
		SeasonalHotelRoomPrice sp = seasonalHotelRoomPriceRepository.findOneByDatesAndRoom(seasonalPrice.getFromDate(),
				seasonalPrice.getToDate(), hr.getId());
		if (sp == null)
			return new MessageDTO("This seasonal price does not exist", ToasterType.ERROR.toString());
		seasonalHotelRoomPriceRepository.delete(sp);
		return new MessageDTO("Seasonal price deleted successfully", ToasterType.SUCCESS.toString());

	}

	public MessageDTO editHotelRoom(HotelRoomDTO hotelRoom, String roomNumber, Hotel hotel) {
		HotelRoom hr = hotelRoomRepository.findOneByNumberAndHotel(roomNumber, hotel.getId());
		if (hr == null)
			return new MessageDTO("Hotel room with this number does not exist", ToasterType.ERROR.toString());
		if (checkForActiveReservations(hr))
			return new MessageDTO("Hotel room has reservations bound to it", ToasterType.ERROR.toString());
		if (!hotelRoom.getRoomNumber().equals(roomNumber)) // if room number was changed, check if new one is taken
			if (hotelRoomRepository.findOneByNumberAndHotel(hotelRoom.getRoomNumber(), hotel.getId()) != null)
				return new MessageDTO("Hotel room with same number already exists", ToasterType.ERROR.toString());
		hr.update(hotelRoom);
		hotelRoomRepository.save(hr);
		return new MessageDTO("Hotel room edited successfully", ToasterType.SUCCESS.toString());
	}

	public ArrayList<HotelRoomDTO> searchRooms(String hotelName, Date fromDate, Date toDate, int forPeople,
			double fromPrice, double toPrice, double fromGrade, double toGrade) {
		ArrayList<HotelRoomDTO> retval = new ArrayList<HotelRoomDTO>();
		Hotel hotel = hotelRepository.findOneByName(hotelName);
		for (HotelRoom hr : hotelRoomRepository.findByHotelPeopleGrade(hotel.getId(), forPeople, fromGrade, toGrade)) {
			boolean hasReservations = false;
			for (HotelReservation res : hr.getNormalReservations()) {
				// (StartA < EndB) and (EndA > StartB)
				if (res.getFromDate().before(toDate) && res.getToDate().after(fromDate)) {
					hasReservations = true;
					break;
				}
			}
			if (!hasReservations)
				for (QuickHotelReservation res : hr.getQuickReservations()) {
					// (StartA < EndB) and (EndA > StartB)
					if (res.getFromDate().before(toDate) && res.getToDate().after(fromDate)) {
						hasReservations = true;
						break;
					}
				}
			if (!hasReservations) {
				HotelRoomDTO hrDTO = new HotelRoomDTO(hr);
				if (hrDTO.getPrice() <= toPrice && hrDTO.getPrice() >= fromPrice) {
					retval.add(hrDTO);
				}
			}
		}
		return retval;
	}

	private boolean checkForActiveReservations(HotelRoom hr) {
		boolean activeReservations = false;
		for (HotelReservation r : hr.getNormalReservations()) {
			if (r.isDone()) {
				activeReservations = true;
				break;
			}
		}

		if (!activeReservations) {
			for (QuickHotelReservation r : hr.getQuickReservations()) {
				if (r.isDone()) {
					activeReservations = true;
					break;
				}
			}
		}
		return activeReservations;
	}
}