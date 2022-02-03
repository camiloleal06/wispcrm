package org.wispcrm.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.Factura;
@Repository
public interface InterfaceFacturas extends CrudRepository<Factura, Integer> {

	Factura  findFirstFacturaByCliente(Cliente cliente);
	
	List<Factura> findFacturaByEstado(boolean f);
	
	@Query(value = "SELECT sum(valor) FROM Factura WHERE estado=true")
    public Long Pendientes();
	
	@Query(value = "SELECT sum(valor) FROM Factura WHERE estado=false and periodo=MONTH(CURRENT_TIMESTAMP)")
    public Long Pagadas();
 }
