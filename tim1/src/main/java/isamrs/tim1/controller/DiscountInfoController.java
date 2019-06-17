package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.service.DiscountInfoService;

@RestController
public class DiscountInfoController {

	@Autowired
	private DiscountInfoService discountInfoService;

	@PreAuthorize("hasRole('SYSADMIN') or hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getDiscountInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DiscountInfo> getDiscountInfo() {
		return new ResponseEntity<DiscountInfo>(discountInfoService.getDiscountInfo(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/editDiscountInfo", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editDiscountInfo(@RequestBody DiscountInfo discountInfo) throws Exception {
		return new ResponseEntity<MessageDTO>(discountInfoService.editDiscountInfo(discountInfo), HttpStatus.OK);
	}

}
