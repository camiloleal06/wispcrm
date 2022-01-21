package org.wispcrm.interfaces;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Pago;
@Repository
public interface InterfacePagos extends PagingAndSortingRepository<Pago,Integer> {

	@Query(value = "SELECT sum(pago) FROM Pago WHERE MONTH(FechaPago)=MONTH(CURRENT_TIMESTAMP)")
    public Long Pagadas();
}
