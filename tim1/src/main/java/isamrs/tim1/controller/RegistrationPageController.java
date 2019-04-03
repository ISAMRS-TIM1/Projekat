package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationPageController {
	@GetMapping("/register")
	public String showRegistrationPage(Map<String, Object> model){
		return "registration/registration.html";
	}
}
