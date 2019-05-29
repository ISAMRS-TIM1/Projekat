package isamrs.tim1.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.BranchOfficeRepository;
import isamrs.tim1.repository.LocationRepository;
import isamrs.tim1.repository.RentACarRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class BranchOfficeService {
	@Autowired
	BranchOfficeRepository branchOfficeRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private RentACarRepository rentACarRepository;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO addBranchOffice(BranchOfficeDTO branch) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();
		if (this.checkIfNameIsTaken(branch.getName(), rentACar)) {
			return new MessageDTO("Branch office name is taken.", ToasterType.ERROR.toString());
		}
		BranchOffice bo = new BranchOffice();
		bo.setId(null);
		Location l = branch.getLocation();
		l.setId(null);
		locationRepository.save(l);
		bo.setLocation(l);
		bo.setName(branch.getName());
		bo.setRentACar(rentACar);

		rentACar.getBranchOffices().add(bo);

		rentACarRepository.save(rentACar);

		return new MessageDTO("Branch office added.", "success");
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editBranchOffice(BranchOfficeDTO branch, String oldName) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		boolean branchExists = false;
		for (BranchOffice bo : rentACar.getBranchOffices()) {
			if (bo.getName().equals(oldName)) {
				branchExists = true;
				break;
			}
		}

		if (!branchExists) {
			return new MessageDTO("Branch office requested for change does not exist.", ToasterType.ERROR.toString());
		}

		if (this.checkIfNameIsTaken(branch.getName(), rentACar)) {
			return new MessageDTO("Branch office name is taken.", ToasterType.ERROR.toString());
		}

		BranchOffice bo = this.findByName(oldName);
		Location l = branch.getLocation();
		l.setId(null);
		locationRepository.save(l);
		bo.setLocation(l);
		bo.setName(branch.getName());
		branchOfficeRepository.save(bo);
		return new MessageDTO("Branch office saved.", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO deleteBranchOffice(String name) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		boolean branchExists = false;
		BranchOffice branch = null;
		for (BranchOffice bo : rentACar.getBranchOffices()) {
			if (bo.getName().equals(name)) {
				branchExists = true;
				branch = bo;
				break;
			}
		}

		if (!branchExists) {
			return new MessageDTO("Branch office requested for deletion does not exist.", ToasterType.ERROR.toString());
		} else if (branch.isDeleted()) {
			return new MessageDTO("Branch office is already deleted.", ToasterType.ERROR.toString());
		}

		Date now = new Date();
		boolean activeReservations = false;
		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getBranchOffice().getName().equals(name) && vr.getToDate().compareTo(now) > 0) {
				activeReservations = true;
				break;
			}
		}

		if (activeReservations) {
			return new MessageDTO("Branch office requested for deletion has active reservations.",
					ToasterType.ERROR.toString());
		}

		branch.setDeleted(true);
		branchOfficeRepository.save(branch);
		rentACarRepository.save(rentACar);
		return new MessageDTO("Branch office deleted.", ToasterType.SUCCESS.toString());
	}

	public boolean checkIfNameIsTaken(String name, RentACar rentACar) {
		if (branchOfficeRepository.findOneByNameAndService(name, rentACar.getId()) != null) {
			return true;
		}
		return false;
	}

	public BranchOffice findByName(String name) {
		return branchOfficeRepository.findOneByName(name);
	}

}
