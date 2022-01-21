package org.wispcrm.interfaces;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Cliente;

@Repository
public interface InterfaceClientes  extends PagingAndSortingRepository <Cliente, Integer> {
	
   Cliente findFirstClienteByIdentificacion(String identificacion);
   Cliente findFirstClienteByEmail(String email);
   Cliente findFirstClienteByTelefono(String telefono);
   Cliente findFirstClienteByDiapago(int diapago);

  
}