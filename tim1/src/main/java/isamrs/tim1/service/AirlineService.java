package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.AirlineDTO;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Location;
import isamrs.tim1.repository.AirlineRepository;

@Service
public class AirlineService {
	
	@Autowired
	private AirlineRepository airlineRepository;
	
	public String editProfile(Airline airline, String oldName) {
		Airline airlineToEdit = airlineRepository.findOneByName(oldName);
        if (airlineToEdit == null) {
            return "Airline with given name does not exist.";
        }
        
        String newName = airline.getName();
		if (airlineRepository.findOneByName(newName) != null)
			return "Name is already in use by some other airline.";
		
		if (newName != null) {
			airlineToEdit.setName(newName);
		}
		
		String newDescription = airline.getDescription();
		if (newDescription != null) {
			airlineToEdit.setDescription(newDescription);
		}
		
		Location newLocation = airline.getLocation();
		if (newLocation != null) {
			airlineToEdit.getLocation().setLatitude(airline.getLocation().getLatitude());
			airlineToEdit.getLocation().setLongitude(airline.getLocation().getLongitude());
		}
		
        try {
        	airlineRepository.save(airlineToEdit);
        }
        catch(Exception e) {
        	System.out.println(e.getMessage());
			return "Database error.";
        }
        
        return null;
	}

	public AirlineDTO getAirline(AirlineAdmin admin) {
		return new AirlineDTO(admin.getAirline());
	}

	public ArrayList<ServiceDTO> getAirlines() {
		ArrayList<ServiceDTO> retval = new ArrayList<ServiceDTO>();
		for(Airline a : airlineRepository.findAll())
			retval.add(new ServiceDTO(a));
		return retval;
	}

}
