package cl.duoc.backend_api.repository;

import cl.duoc.backend_api.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}