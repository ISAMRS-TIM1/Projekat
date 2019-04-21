package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.repository.LocationRepository;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.service.BranchOfficeService;

@RestController
public class BranchOfficeController {
	@Autowired
	private BranchOfficeService branchOfficeService;

	@Autowired
	private RentACarRepository rentACarRepository;

	@Autowired
	private LocationRepository locationRepository;

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/addBranchOffice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addBranchOffice(@RequestBody BranchOfficeDTO branch) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();
		if (branchOfficeService.checkIfNameIsTaken(branch.getName(), rentACar)) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office name is taken.", "error"),
					HttpStatus.OK);
		}
		BranchOffice bo = new BranchOffice();
		bo.setId(null);
		Location l;
		if((l = locationRepository.findOneByLatitudeAndLongitude(branch.getLocation().getLatitude(), branch.getLocation().getLongitude())) == null) {
			l = branch.getLocation();
			l.setId(null);
			locationRepository.save(l);
		}
		bo.setLocation(l);
		bo.setName(branch.getName());
		bo.setRentACar(rentACar);

		rentACar.getBranchOffices().add(bo);

		rentACarRepository.save(rentACar);

		return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office added.", "success"), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/editBranchOffice/{oldName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editBranchOffice(@RequestBody BranchOfficeDTO branch,
			@PathVariable("oldName") String oldName) {
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
		Location l;
		if((l = locationRepository.findOneByLatitudeAndLongitude(branch.getLocation().getLatitude(), branch.getLocation().getLongitude())) == null) {
			l = branch.getLocation();
			l.setId(null);
			locationRepository.save(l);
		}
		bo.setLocation(l);
		bo.setName(branch.getName());

		branchOfficeService.save(bo);
		return new ResponseEntity<MessageDTO>(new MessageDTO("Branch office saved.", "success"), HttpStatus.OK);
	}
}
