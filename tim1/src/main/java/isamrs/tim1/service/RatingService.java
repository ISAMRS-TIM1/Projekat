package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.ServiceGradeDTO;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.repository.ServiceRepository;

@Service
public class RatingService {

	@Autowired
	private ServiceRepository serviceRepository;

	public void rateService(ServiceGradeDTO serviceGrade) {
		isamrs.tim1.model.Service s = serviceRepository.findOneByName(serviceGrade.getServiceName());
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ServiceGrade g = new ServiceGrade(user, s, serviceGrade.getGrade());

		if (s.getServiceGrades().contains(g)) {
			s.getServiceGrades().remove(g);
			s.getServiceGrades().add(g);
		} else {
			s.getServiceGrades().add(g);
		}

		serviceRepository.save(s);
	}
}
