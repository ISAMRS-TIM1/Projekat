package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.repository.DiscountInfoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class DiscountInfoService {
	@Autowired
	private DiscountInfoRepository discountInfoRepository;

	public DiscountInfo getDiscountInfo() {
		return discountInfoRepository.findAll().get(0);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editDiscountInfo(DiscountInfo discountInfo) {
		discountInfoRepository.deleteAll();
		discountInfoRepository.save(discountInfo);
		return new MessageDTO("Discount parameters successfully updated", ToasterType.SUCCESS.toString());
	}
}
