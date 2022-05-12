package org.wispcrm.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.EditarClienteDTO;

@Repository
public interface ClienteDao extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findFirstClienteByIdentificacion(String identificacion);

    Optional<Cliente> findFirstClienteByEmail(String email);

    Optional<Cliente> findFirstClienteByTelefono(String telefono);

    Optional<Cliente> findFirstClienteByDiaPago(int diapago);

    @Query("SELECT new org.wispcrm.modelo.ClienteDTO(c.id, c.identificacion, CONCAT(c.nombres,' ',c.apellidos), "
            + "c.email, c.telefono,  p.precio, c.estado) " + " FROM Cliente c JOIN c.planes p ")
    List<ClienteDTO> lista();

    @Query("SELECT new org.wispcrm.modelo.EditarClienteDTO(c.id, c.identificacion, c.nombres, c.apellidos , "
            + " c.email, c.direccion, c.telefono, p.nombre, p.id, c.diaPago) "
            + " FROM Cliente c JOIN c.planes p  WHERE c.id = :id")
    EditarClienteDTO editarCliente(@Param("id") Integer id);

    @Query("SELECT new org.wispcrm.modelo.ClienteDTO(c.id, c.identificacion, CONCAT(c.nombres,' ',c.apellidos), "
            + "c.email , c.telefono, p.precio, c.estado) " + " FROM Cliente c JOIN c.planes p ")
    Page<ClienteDTO> listaPaginada(Pageable pageable);

}