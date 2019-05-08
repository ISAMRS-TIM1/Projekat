package isamrs.tim1.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.QuickHotelReservation;
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

	public Map<String, Long> getDailyChartData() {
		return getChartData(new SimpleDateFormat("dd/MM/yyyy"));
	}

	public Map<String, Long> getWeeklyChartData() {
		return getChartData(new SimpleDateFormat("MM/yyyy 'week: 'W"));
	}

	public Map<String, Long> getMonthlyChartData() {
		return getChartData(new SimpleDateFormat("MM/yyyy"));
	}
	
	private Map<String, Long> getChartData(SimpleDateFormat sdf){
		Hotel hotel = ((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getHotel();

		ArrayList<HotelReservation> doneNormalReservations = new ArrayList<HotelReservation>();

		for (HotelReservation hr : hotel.getNormalReservations()) {
			if (hr.isDone()) {
				doneNormalReservations.add(hr);
			}
		}

		ArrayList<QuickHotelReservation> doneQuickReservations = new ArrayList<QuickHotelReservation>();

		for (QuickHotelReservation qr : hotel.getQuickReservations()) {
			if (qr.isDone()) {
				doneQuickReservations.add(qr);
			}
		}

		Map<String, Long> normalCounts = doneNormalReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> quickCounts = doneQuickReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));

		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);
		quickCounts.forEach((key, value) -> returnValue.merge(key, value, (v1, v2) -> v1 + v2));

		return returnValue;
	}

}
