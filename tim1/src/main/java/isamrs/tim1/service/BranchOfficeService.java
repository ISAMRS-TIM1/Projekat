package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.RentACar;
import isamrs.tim1.repository.BranchOfficeRepository;

@Service
public class BranchOfficeService {
	@Autowired
	BranchOfficeRepository branchOfficeRepository;

	public boolean checkIfNameIsTaken(String name, RentACar rentACar) {
		if (branchOfficeRepository.findOneByNameAndService(name, rentACar.getId()) != null) {
			return true;
		}
		return false;
	}
}
