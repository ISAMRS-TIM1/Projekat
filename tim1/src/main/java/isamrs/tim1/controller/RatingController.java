package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.ServiceGradeDTO;
import isamrs.tim1.service.RatingService;

@RestController
public class RatingController {

	@Autowired
	private RatingService ratingService;

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/rateService", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void rateService(@RequestBody ServiceGradeDTO serviceGrade) {
		ratingService.rateService(serviceGrade);
	}
}
