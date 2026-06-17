package cl.duoc.backend_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.backend_api.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByIdPedido(Long idPedido);
    List<Pago> findByEstado(String estado);
}
