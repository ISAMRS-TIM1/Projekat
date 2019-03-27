package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.repository.RentACarRepository;

@Service
public class RentACarService {
	@Autowired
	private RentACarRepository rentACarRepository;

	public boolean editProfile(RentACar rentACar, String oldName) {
		RentACar rentACarToEdit = rentACarRepository.findOneByName(oldName);
		if (rentACarToEdit == null) {
			return false;
		}

		String newDescription = rentACar.getDescription();
		if (newDescription != null) {
			rentACarToEdit.setDescription(newDescription);
		}

		String newName = rentACar.getName();
		if (newName != null) {
			rentACarToEdit.setName(newName);
		}

		Location newLocation = rentACar.getLocation();
		if (newLocation != null) {
			rentACarToEdit.getLocation().setLatitude(rentACar.getLocation().getLatitude());
			rentACarToEdit.getLocation().setLongitude(rentACar.getLocation().getLongitude());
		}

		try {
			rentACarRepository.save(rentACarToEdit);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
