package org.wispcrm.interfaceservice;

import java.util.List;
import org.wispcrm.modelo.Plan;

public interface InterfacePlanService {
	
	public List<Plan> findAll();

	public void save(Plan plan);

	public Plan findOne(Integer id);

}
