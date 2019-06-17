package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisteredUserHomepageController {
	@GetMapping(value = { "/registeredUser", "/" })
	public String showUserHomepage(Map<String, Object> model) {
		return "registeredUser/index.html";
	}
}
