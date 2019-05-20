package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.QuickVehicleReservationDTO;
import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
public class RentACarService {
	@Autowired
	private RentACarRepository rentACarRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	public String editProfile(RentACar rentACar, String oldName) {
		RentACar rentACarToEdit = rentACarRepository.findOneByName(oldName);
		if (rentACarToEdit == null) {
			return "Edited rent a car service does not exist.";
		}

		String newName = rentACar.getName();
		if (newName != null) {
			if (serviceRepository.findOneByName(newName) == null) {
				rentACarToEdit.setName(newName);
			} else {
				return "Name is already in use by some other service.";
			}
		}

		String newDescription = rentACar.getDescription();
		if (newDescription != null) {
			rentACarToEdit.setDescription(newDescription);
		}

		Location newLocation = rentACar.getLocation();
		if (newLocation != null) {
			rentACarToEdit.getLocation().setLatitude(rentACar.getLocation().getLatitude());
			rentACarToEdit.getLocation().setLongitude(rentACar.getLocation().getLongitude());
		}

		try {
			rentACarRepository.save(rentACarToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Database error.";
		}

		return null;
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

	public MessageDTO createQuickVehicleReservation(QuickVehicleReservationDTO quickReservation) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String branchName = quickReservation.getBranchOfficeName();
		String producer = quickReservation.getVehicleProducer();
		String model = quickReservation.getVehicleModel();

		Date from = null;
		try {
			from = sdf.parse(quickReservation.getFromDate());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Date to = null;
		try {
			to = sdf.parse(quickReservation.getToDate());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getBranchOffice().getName().equals(branchName) && vr.getVehicle().getProducer().equals(producer)
					&& vr.getVehicle().getModel().equals(model) && vr.getFromDate().compareTo(from) <= 0
					&& vr.getToDate().compareTo(to) >= 0) {
				return new MessageDTO("Vehicle is taken in given period", ToasterType.ERROR.toString());
			}
		}

		QuickVehicleReservation newQuickReservation = new QuickVehicleReservation();

		BranchOffice br = rentACar.getBranchOffices().stream()
				.filter(bo -> bo.getName().equals(quickReservation.getBranchOfficeName())).findFirst().orElse(null);
		newQuickReservation.setBranchOffice(br);
		newQuickReservation.setDiscount(quickReservation.getDiscount());
		try {
			newQuickReservation.setFromDate(sdf.parse(quickReservation.getFromDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			newQuickReservation.setToDate(sdf.parse(quickReservation.getToDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newQuickReservation.setId(null);
		Vehicle v = rentACar.getVehicles().stream()
				.filter(ve -> ve.getProducer().equals(quickReservation.getVehicleProducer())
						&& ve.getModel().equals(quickReservation.getVehicleModel()))
				.findFirst().orElse(null);
		newQuickReservation.setVehicle(v);
		newQuickReservation.setFlightReservation(null);

		int numberOfDays = (int) ((newQuickReservation.getFromDate().getTime()
				- newQuickReservation.getToDate().getTime()) / (1000 * 60 * 60 * 24));

		if (numberOfDays == 0) {
			numberOfDays = 1;
		}
		newQuickReservation.setPrice(numberOfDays * newQuickReservation.getDiscount() / 100.0);
		rentACar.getReservations().add(newQuickReservation);

		rentACarRepository.save(rentACar);

		return new MessageDTO("Quick vehicle reservation added successfully", ToasterType.SUCCESS.toString());
	}

	public ArrayList<QuickVehicleReservationDTO> getQuickVehicleReservations() {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		ArrayList<QuickVehicleReservationDTO> quickReservations = new ArrayList<QuickVehicleReservationDTO>();

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr instanceof QuickVehicleReservation) {
				QuickVehicleReservation qvr = (QuickVehicleReservation) vr;

				if (qvr.getFlightReservation() == null) {
					quickReservations.add(new QuickVehicleReservationDTO(qvr));
				}
			}
		}

		return quickReservations;
	}
}
