package org.wispcrm.interfaces;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Plan;

@Repository
public interface InterfacePlanes extends PagingAndSortingRepository <Plan, Integer> {


}
