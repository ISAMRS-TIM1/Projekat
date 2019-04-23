package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.service.WebSocketService;

@RestController
public class WebSocketController {
	
	@Autowired
	private WebSocketService webSocketService;
	
	/*
	 * Primer slanja poruke gadjanjem REST kontrolera.
	 */
	@RequestMapping(value = "/sendInvitation", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> sendInvitation(@RequestParam String invitedUser){
		return webSocketService.sendInvitation(invitedUser);
	}

}
