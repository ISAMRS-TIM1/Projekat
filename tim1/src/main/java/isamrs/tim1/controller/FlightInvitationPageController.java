package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FlightInvitationPageController {
	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@GetMapping("/flightInvitation")
	public String showFlightInvitationPage(Map<String, Object> model){
		return "flightInvitation/index.html";
	}
}
