package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarAdminPageController {
	@GetMapping("/carAdmin")
	public String showMainPage(Map<String, Object> model) {
		return "carAdmin/index.html";
	}
}
