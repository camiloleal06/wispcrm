package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaceservice.InterfacePlanService;
import org.wispcrm.interfaces.InterfacePlanes;
import org.wispcrm.modelo.Plan;

@Service
public class PlanServices implements InterfacePlanService{

	@Autowired
	private InterfacePlanes planesDao;
	
	
	@Override
	public List<Plan> findAll() {
			return (List<Plan>) planesDao.findAll();
	}

   @Override
	public void save(Plan plan) {
			planesDao.save(plan);
		
	}

	@Override
	public Plan findOne(Integer id) {

		return planesDao.findById(id).orElse(null);
	}


}
