package org.wispcrm.interfaceService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wispcrm.modelo.Plan;

public interface InterfacePlanService {
	
	public List<Plan> findAll();

	public Page<Plan> findAll(Pageable pageable);

	public void save(Plan plan);

	public Plan findOne(Integer id);

	public void delete(Integer id);
}
