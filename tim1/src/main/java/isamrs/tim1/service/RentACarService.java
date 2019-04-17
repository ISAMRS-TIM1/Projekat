package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.repository.RentACarRepository;

@Service
public class RentACarService {
	@Autowired
	private RentACarRepository rentACarRepository;

	public String editProfile(RentACar rentACar, String oldName) {
		RentACar rentACarToEdit = rentACarRepository.findOneByName(oldName);
		if (rentACarToEdit == null) {
			return "Edited rent a car service does not exist.";
		}

		String newName = rentACar.getName();
		if (newName != null) {
			if (rentACarRepository.findOneByName(newName) == null) {
				rentACarToEdit.setName(newName);
			} else {
				return "Name is already in use by some other rent a car service.";
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

	public ArrayList<ServiceDTO> getRentACars() {
		ArrayList<ServiceDTO> retval = new ArrayList<ServiceDTO>();
		for(RentACar r : rentACarRepository.findAll())
			retval.add(new ServiceDTO(r));
		return retval;
	}
}
