package com.user.test.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.user.test.enums.Model;
import com.user.test.model.LaptopModel;
import com.user.test.repository.LaptopRepository;

public class LaptopService {
	
	@Autowired
	LaptopRepository laptoprepository;
	
	@PostConstruct
	public void init() {
		for(Model model: Model.values()) {
			Boolean isExist = laptoprepository.existsByModel(model);
			
			if(Boolean.FALSE.equals(isExist)) {
				LaptopModel laptopToBeAdded = LaptopModel.builder()
						.model(model)
						.build();
				
				
				laptoprepository.save(laptopToBeAdded);
			}
		}
	}
}
