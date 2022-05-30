package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wispcrm.excepciones.NotFoundException;
import org.wispcrm.interfaces.PlanInterface;
import org.wispcrm.daos.PlanDao;
import org.wispcrm.modelo.Plan;

@Service
public class PlanServiceImpl implements PlanInterface {

	@Autowired
	private PlanDao planesDao;
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
		return planesDao.findById(id).orElseThrow(()->new NotFoundException("No existe el Plan"));
	}

}
