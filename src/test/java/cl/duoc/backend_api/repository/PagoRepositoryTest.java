package cl.duoc.backend_api.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import cl.duoc.backend_api.model.Pago;

@DataJpaTest
class PagoRepositoryTest {

    @Autowired
    private PagoRepository pagoRepository;

    @Test
    void testGuardarPago() {
        Pago pago = new Pago();
        pago.setNombreCliente("Juan");
        pago.setMetodoPago("TARJETA");
        pago.setMonto(10000.0);
        pago.setEstado("APROBADO");

        Pago guardado = pagoRepository.save(pago);

        assertNotNull(guardado.getId());
        assertEquals("Juan", guardado.getNombreCliente());
    }

    @Test
    void testBuscarPorId() {
        Pago pago = new Pago();
        pago.setNombreCliente("María");
        pago.setMetodoPago("EFECTIVO");
        pago.setMonto(5000.0);
        pago.setEstado("APROBADO");

        Pago guardado = pagoRepository.save(pago);
        Optional<Pago> encontrado = pagoRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("María", encontrado.get().getNombreCliente());
    }

    @Test
    void testListarTodos() {
        Pago p1 = new Pago();
        p1.setNombreCliente("Cliente 1");
        p1.setMetodoPago("TARJETA");
        p1.setMonto(1000.0);
        p1.setEstado("APROBADO");

        Pago p2 = new Pago();
        p2.setNombreCliente("Cliente 2");
        p2.setMetodoPago("EFECTIVO");
        p2.setMonto(2000.0);
        p2.setEstado("CANCELADO");

        pagoRepository.save(p1);
        pagoRepository.save(p2);

        List<Pago> lista = pagoRepository.findAll();

        assertTrue(lista.size() >= 2);
    }
}