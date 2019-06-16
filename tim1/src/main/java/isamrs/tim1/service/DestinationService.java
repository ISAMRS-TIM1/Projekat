package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.DestinationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Destination;
import isamrs.tim1.model.Location;
import isamrs.tim1.repository.DestinationRepository;
import isamrs.tim1.repository.LocationRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class DestinationService {

	@Autowired
	DestinationRepository destinationRepository;
	
	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired
	LocationRepository locationRepository;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> addDestination(DestinationDTO destDTO) {
		AirlineAdmin admin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Airline a = admin.getAirline();
	    if (a == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("Airline does not exist.", ""), HttpStatus.BAD_REQUEST);
	    Destination dest = destinationRepository.findOneByName(destDTO.getNameOfDest());
	    if (dest != null)
	    	return new ResponseEntity<MessageDTO>(new MessageDTO("Destination with same name already exists.", ""), HttpStatus.OK);
		Destination d = new Destination();
		d.setName(destDTO.getNameOfDest());
		Location l = new Location();
		l.setLatitude(destDTO.getLatitude());
		l.setLongitude(destDTO.getLongitude());
		d.setLocation(l);
		d.setAirline(a);
		a.getDestinations().add(d);
		locationRepository.save(l);
		destinationRepository.save(d);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Destination added successfully", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	public ArrayList<String> getDestinations() {
		ArrayList<Destination> destinations = (ArrayList<Destination>) destinationRepository.findAll();
		ArrayList<String> destList = new ArrayList<String>();
		for (Destination d : destinations) {
			if (!destList.contains(d.getName())) {
				destList.add(d.getName());
			}
		}
		return destList;
	}

	public ResponseEntity<DestinationDTO> loadDestination(String dest) {
		Destination d = destinationRepository.findOneByName(dest);
		return new ResponseEntity<DestinationDTO>(new DestinationDTO(d), HttpStatus.OK);
	}
}
