package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.service.BranchOfficeService;

@RestController
public class BranchOfficeController {
	@Autowired
	private BranchOfficeService branchOfficeService;

	@Autowired
	private RentACarRepository rentACarRepository;

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/addBranchOffice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addBranchOffice(BranchOfficeDTO branch) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();
		if (branchOfficeService.checkIfNameIsTaken(branch.getName(), rentACar)) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office name is taken.", "error"),
					HttpStatus.OK);
		}
		BranchOffice bo = new BranchOffice();
		bo.setId(null);
		bo.setLocation(branch.getLocation());
		bo.setName(bo.getName());
		bo.setRentACar(rentACar);

		rentACar.getBranchOffices().add(bo);

		rentACarRepository.save(rentACar);

		return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office added.", "success"), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/editBranchOffice", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editBranchOffice(BranchOfficeDTO branch,
			@RequestParam(required = true) String oldName) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		boolean branchExists = false;
		for (BranchOffice bo : rentACar.getBranchOffices()) {
			if (bo.getName().equals(oldName)) {
				branchExists = true;
			}
		}

		if (!branchExists) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Branch office requested for change does not exist.", "error"), HttpStatus.OK);
		}

		if (branchOfficeService.checkIfNameIsTaken(branch.getName(), rentACar)) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office name is taken.", "error"),
					HttpStatus.OK);
		}
		BranchOffice bo = branchOfficeService.findByName(oldName);
		bo.setLocation(branch.getLocation());
		bo.setName(bo.getName());

		branchOfficeService.save(bo);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office saved.", "success"), HttpStatus.OK);
	}
}
