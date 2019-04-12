package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationVerificationPageController {
	@GetMapping("registration/verified")
	public String showVerifiedPage(Map<String, Object> model){
		return "registration/registrationConfirmed.html";
	}
	
	@GetMapping("registration/notVerified")
	public String showNowVerifiedPage(Map<String, Object> model){
		return "registration/registrationNotConfirmed.html";
	}
}
