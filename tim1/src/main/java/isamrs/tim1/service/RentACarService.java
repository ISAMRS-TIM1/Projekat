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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class RentACarService {
	@Autowired
	private RentACarRepository rentACarRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editRentACarProfile(RentACarDTO rentACar) {
		RentACar rentACarToEdit = rentACarRepository.findOneByName(rentACar.getOldName());
		if (rentACarToEdit == null) {
			return new MessageDTO("Edited rent a car service does not exist.", ToasterType.ERROR.toString());
		}
		
		if(!rentACar.getOldName().equals(rentACar.getName()) && serviceRepository.findOneByName(rentACar.getName()) != null) {
			return new MessageDTO("Name is already in use by some other service.", ToasterType.ERROR.toString());
		}
		rentACarToEdit.setName(rentACar.getName());
		rentACarToEdit.setDescription(rentACar.getDescription());
		Location l = new Location(rentACar.getLatitude(), rentACar.getLongitude());
		rentACarToEdit.setLocation(l);

		rentACarRepository.save(rentACarToEdit);

		return new MessageDTO("Rentacar edit successfull", ToasterType.SUCCESS.toString());
	}

	public RentACarDTO getRentACarInfo(String rentACarName) {
		RentACar rentACarToReturn = rentACarRepository.findOneByName(rentACarName);

		if (rentACarToReturn != null) {
			return new RentACarDTO(rentACarToReturn);
		} else {
			return null;
		}
	}

	public ArrayList<ServiceViewDTO> getRentACars() {
		ArrayList<ServiceViewDTO> retval = new ArrayList<ServiceViewDTO>();
		for (RentACar r : rentACarRepository.findAll())
			retval.add(new ServiceViewDTO(r));
		return retval;
	}

	public DetailedServiceDTO getRentACar(String name) {
		return new DetailedServiceDTO(rentACarRepository.findOneByName(name));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO addRentACar(RentACar rentACar) {
		if (serviceRepository.findOneByName(rentACar.getName()) != null)
			return new MessageDTO("Service with the same name already exists.", ToasterType.ERROR.toString());
		rentACar.setId(null); // to ensure INSERT command
		rentACarRepository.save(rentACar);
		return new MessageDTO("Rent a car service successfully added", ToasterType.SUCCESS.toString());
	}

	public ArrayList<BranchOfficeDTO> getBranchOffices() {
		RentACarAdmin admin = (RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<BranchOfficeDTO> branchOffices = new ArrayList<>();

		for (BranchOffice bo : admin.getRentACar().getBranchOffices()) {
			branchOffices.add(new BranchOfficeDTO(bo));
		}

		return branchOffices;
	}

	public Map<String, Long> getDailyGraphData() {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		ArrayList<VehicleReservation> doneReservations = new ArrayList<VehicleReservation>();

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getFlightReservation() != null && vr.getFlightReservation().getDone()) {
				doneReservations.add(vr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Long> normalCounts = doneReservations.stream().collect(Collectors
				.groupingBy(r -> sdf.format(r.getFlightReservation().getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;
	}

	public Map<String, Long> getWeeklyGraphData() {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		ArrayList<VehicleReservation> doneReservations = new ArrayList<VehicleReservation>();

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getFlightReservation() != null && vr.getFlightReservation().getDone()) {
				doneReservations.add(vr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy 'week: 'W");

		Map<String, Long> normalCounts = doneReservations.stream().collect(Collectors
				.groupingBy(r -> sdf.format(r.getFlightReservation().getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;
	}

	public Map<String, Long> getMonthlyGraphData() {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		ArrayList<VehicleReservation> doneReservations = new ArrayList<VehicleReservation>();

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getFlightReservation() != null && vr.getFlightReservation().getDone()) {
				doneReservations.add(vr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		Map<String, Long> normalCounts = doneReservations.stream().collect(Collectors
				.groupingBy(r -> sdf.format(r.getFlightReservation().getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;

	}

	public double getIncomeOfRentACar(Date fromDate, Date toDate) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();
		double income = 0;

		for (VehicleReservation vr : rentACar.getReservations()) {
			FlightReservation fr = vr.getFlightReservation();
			if (fr.getDone() && fr.getDateOfReservation().after(fromDate) && fr.getDateOfReservation().before(toDate)) {
				income += vr.getPrice();
			}
		}

		return income;
	}
}
