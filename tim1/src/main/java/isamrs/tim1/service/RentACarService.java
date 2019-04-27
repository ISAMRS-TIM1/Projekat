package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
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
}
