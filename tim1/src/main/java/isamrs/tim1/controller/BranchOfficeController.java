package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.service.BranchOfficeService;

@RestController
public class BranchOfficeController {
	@Autowired
	private BranchOfficeService branchOfficeService;

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/addBranchOffice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addBranchOffice(@RequestBody BranchOfficeDTO branch) {
		return new ResponseEntity<MessageDTO>(branchOfficeService.addBranchOffice(branch), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/editBranchOffice/{oldName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editBranchOffice(@RequestBody BranchOfficeDTO branch,
			@PathVariable("oldName") String oldName) {
		return new ResponseEntity<MessageDTO>(branchOfficeService.editBranchOffice(branch, oldName), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/deleteBranchOffice/{name}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> deleteBranchOffice(@PathVariable("name") String name) {
		return new ResponseEntity<MessageDTO>(branchOfficeService.deleteBranchOffice(name), HttpStatus.OK);
	}

}
