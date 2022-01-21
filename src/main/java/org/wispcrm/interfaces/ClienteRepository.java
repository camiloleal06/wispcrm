package org.wispcrm.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wispcrm.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    @Query("SELECT p FROM Cliente p WHERE"
            + "  p.nombres LIKE %?1%")
    public List<Cliente> search(String keyword);
}