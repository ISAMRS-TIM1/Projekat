package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.VerificationToken;
import isamrs.tim1.repository.VerificationTokenRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class VerificationTokenService {
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveToken(VerificationToken token) {
		verificationTokenRepository.save(token);
	}

}
