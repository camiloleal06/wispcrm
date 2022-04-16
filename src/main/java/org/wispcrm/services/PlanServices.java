package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
		// TODO Auto-generated method stub
		return (List<Plan>) planesDao.findAll(); 
	}

	@Override
	public Page<Plan> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Plan plan) {
		// TODO Auto-generated method stub
		planesDao.save(plan);
		
	}

	@Override
	public Plan findOne(Integer id) {
		// TODO Auto-generated method stub
		return planesDao.findById(id).orElse(null);
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

}
