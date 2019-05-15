package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import isamrs.tim1.dto.HotelAdditionalServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.service.HotelAdditionalServicesService;

@Controller
public class HotelAdditionalServicesController {
	
	@Autowired
	private HotelAdditionalServicesService hotelAdditionalServicesService;

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/addAdditionalService", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addAdditionalService(@RequestBody HotelAdditionalServiceDTO additionalService) {
		return new ResponseEntity<MessageDTO>(hotelAdditionalServicesService.addAdditionalService(additionalService,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}
}
