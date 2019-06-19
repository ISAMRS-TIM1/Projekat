package isamrs.tim1.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HotelAdminPageController {
	@GetMapping("/hotelAdmin")
	public String showMainPage(Map<String, Object> model) {
		return "hotelAdmin/index.html";
	}
}
