package com.user.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.test.enums.Model;
import com.user.test.model.LaptopModel;

public interface LaptopRepository extends JpaRepository<LaptopModel, Integer>{
	Boolean existsByModel(Model model);
}
