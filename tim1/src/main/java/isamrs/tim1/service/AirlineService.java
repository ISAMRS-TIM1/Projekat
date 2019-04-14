package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.AirlineDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.Seat;
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
		AirlineDTO a = new AirlineDTO(admin.getAirline());
		PlaneSegment p1 = new PlaneSegment();
		p1.setSegmentClass(PlaneSegmentClass.FIRST);
		PlaneSegment p2 = new PlaneSegment();
		p2.setSegmentClass(PlaneSegmentClass.BUSINESS);
		PlaneSegment p3 = new PlaneSegment();
		p3.setSegmentClass(PlaneSegmentClass.ECONOMY);
		Seat s1 = new Seat();
		s1.setNumber(1);
		Seat s2 = new Seat();
		s2.setNumber(2);
		Seat s3 = new Seat();
		s3.setNumber(3);
		Seat s4 = new Seat();
		s4.setNumber(4);
		Seat s5 = new Seat();
		s5.setNumber(5);
		Seat s6 = new Seat();
		s6.setNumber(6);
		Seat s7 = new Seat();
		s7.setNumber(7);
		p1.getSeats().add(s1);
		p1.getSeats().add(s2);
		p2.getSeats().add(s3);
		p2.getSeats().add(s4);
		p2.getSeats().add(s5);
		p3.getSeats().add(s6);
		p3.getSeats().add(s7);
		a.getPlaneSegments().add(p1);
		a.getPlaneSegments().add(p2);
		a.getPlaneSegments().add(p3);
		return a;
	}

}
