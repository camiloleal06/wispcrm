package org.wispcrm.daos;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Plan;

@Repository
public interface PlanDao extends PagingAndSortingRepository <Plan, Integer> {


}
