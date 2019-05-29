package isamrs.tim1;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import isamrs.tim1.model.Authority;
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.model.User;
import isamrs.tim1.model.UserType;
import isamrs.tim1.repository.DiscountInfoRepository;
import isamrs.tim1.repository.UserRepository;
import isamrs.tim1.service.CustomUserDetailsService;

@SpringBootApplication
@EnableTransactionManagement
public class ProjekatApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DiscountInfoRepository discountInfoRepository;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Value("${sysadmin.email}")
	private String sysadminEmail;

	@Value("${sysadmin.pass}")
	private String sysadminPassword;

	@Value("${discount.percentagePerPoint}")
	private double discountPercentagePerPoint;

	@Value("${discount.kmsNeededForPoint}")
	private double kmsNeededForPoint;

	public static void main(String[] args) {
		SpringApplication.run(ProjekatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (userRepository.findOneByEmail(sysadminEmail) == null) {
			User sysadmin = new User();
			sysadmin.setId(null);
			sysadmin.setAddress("Adresa");
			sysadmin.setEmail(sysadminEmail);
			sysadmin.setEnabled(true);
			sysadmin.setFirstName("Sistemski");
			sysadmin.setLastName("Admin");
			sysadmin.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
			sysadmin.setPassword(userDetailsService.encodePassword(sysadminPassword));
			sysadmin.setPasswordChanged(true);
			sysadmin.setPhoneNumber("+381635486254");
			sysadmin.setVerified(true);
			List<Authority> authorities = new ArrayList<Authority>();
			Authority a = new Authority();
			a.setType(UserType.ROLE_SYSADMIN);
			authorities.add(a);
			sysadmin.setAuthorities(authorities);
			userRepository.save(sysadmin);
		}
		if (discountInfoRepository.findAll().isEmpty()) {
			DiscountInfo di = new DiscountInfo();
			di.setId(null);
			di.setDiscountPercentagePerPoint(discountPercentagePerPoint);
			di.setKmsNeededForPoint(kmsNeededForPoint);
			discountInfoRepository.save(di);
		}

	}

}
