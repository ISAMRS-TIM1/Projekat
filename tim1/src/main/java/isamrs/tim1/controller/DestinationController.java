package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.DestinationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.service.DestinationService;

@RestController
public class DestinationController {
	
	@Autowired
	private DestinationService destinationService;
	
	@RequestMapping(value = "/api/addDestination", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> addDestination(@RequestBody DestinationDTO d) {
		return destinationService.addDestination(d);
	}
	
	@RequestMapping(value = "/api/getDestinations", method = RequestMethod.GET)
	public ArrayList<String> getDestinations() {
		return destinationService.getDestinations();
	}
	
	@RequestMapping(value = "/api/loadDestination", method = RequestMethod.GET)
	public ResponseEntity<DestinationDTO> loadDestination(@RequestParam String dest) {
		return destinationService.loadDestination(dest);
	}
}
