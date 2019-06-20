package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Set;

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
			return new ResponseEntity<MessageDTO>(new MessageDTO("Airline does not exist.", ToasterType.ERROR.toString()), HttpStatus.BAD_REQUEST);
	    if (!destDTO.getNameOfDest().equals(destDTO.getOldName())) {
	    	Destination dest = destinationRepository.findOneByName(destDTO.getNameOfDest());
		    if (dest != null)
		    	return new ResponseEntity<MessageDTO>(new MessageDTO("Destination with same name already exists.", ToasterType.ERROR.toString()), HttpStatus.OK);
	    }
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

	public ResponseEntity<ArrayList<DestinationDTO>> getDestinationsOfAirline() {
		AirlineAdmin admin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Airline a = admin.getAirline();
	    if (a == null)
			return null;
	    Set<Destination> dests = destinationRepository.findByAirline(a);
	    ArrayList<DestinationDTO> destList = new ArrayList<DestinationDTO>();
	    for (Destination d : dests) {
	    	destList.add(new DestinationDTO(d));
	    }
		return new ResponseEntity<ArrayList<DestinationDTO>>(destList, HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> editDestination(DestinationDTO destDTO) {
		AirlineAdmin admin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Airline a = admin.getAirline();
	    if (a == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("Airline does not exist.", ToasterType.ERROR.toString()), HttpStatus.BAD_REQUEST);
	    if (!destDTO.getOldName().equals(destDTO.getNameOfDest())) {
	    	Destination destNew = destinationRepository.findOneByName(destDTO.getNameOfDest());
		    if (destNew != null)
		    	return new ResponseEntity<MessageDTO>(new MessageDTO("Destination with same name already exists.", ToasterType.ERROR.toString()), HttpStatus.OK);
	    }
	    Destination dest = destinationRepository.findOneByName(destDTO.getOldName());
	    if (dest == null)
	    	return new ResponseEntity<MessageDTO>(new MessageDTO("Destination does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
	    if (destDTO.getNameOfDest() != null) {
			dest.setName(destDTO.getNameOfDest());
		}
		Location l = dest.getLocation();
		if (destDTO.getLatitude() != null) {
			l.setLatitude(destDTO.getLatitude());
		}
		if (destDTO.getLongitude() != null) {
			l.setLongitude(destDTO.getLongitude());
		}
		destinationRepository.save(dest);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Destination edited successfully", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
}
