package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.Airline;
import isamrs.tim1.repository.AirlineRepository;

@Service
public class AirlineService {
	
	@Autowired
	private AirlineRepository airlineRepository;
	
	public Boolean editProfile(Airline airline, String name) {
		Airline airlineToEdit = airlineRepository.findOneByName(name);
        if (airlineToEdit == null) {
            return false;
        }
        airlineToEdit.setName(airline.getName());
        airlineToEdit.setDescription(airline.getDescription());
        if (airline.getLocation() != null) {
        	airlineToEdit.getLocation().setLatitude(airline.getLocation().getLatitude());
        	airlineToEdit.getLocation().setLongitude(airline.getLocation().getLongitude());
        }
        try {
        	airlineRepository.save(airlineToEdit);
        }
        catch(Exception e) {
        	return false;
        }
        
        return true;
	}

}
