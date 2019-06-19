package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FlightInvitationPageController {
	@GetMapping("/flightInvitation")
	public String showFlightInvitationPage(Map<String, Object> model) {
		return "flightInvitation/index.html";
	}
}
