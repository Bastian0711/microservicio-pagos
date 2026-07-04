package cl.duoc.backend_api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class PagoTest {

    @Test
    void testCrearPagoConDatosValidos() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setIdPedido(10L);
        pago.setNombreCliente("Juan Pérez");
        pago.setMetodoPago("TARJETA");
        pago.setMonto(50000.0);
        pago.setEstado("APROBADO");

        assertEquals(1L, pago.getId());
        assertEquals(10L, pago.getIdPedido());
        assertEquals("Juan Pérez", pago.getNombreCliente());
        assertEquals("TARJETA", pago.getMetodoPago());
        assertEquals(50000.0, pago.getMonto());
        assertEquals("APROBADO", pago.getEstado());
    }

    @Test
    void testEstadosCancelacionYDevolucion() {
        Pago pago = new Pago();
        pago.setEstado("CANCELADO");
        pago.setMotivoCancelacion("Cliente solicitó cancelación");

        assertEquals("CANCELADO", pago.getEstado());
        assertEquals("Cliente solicitó cancelación", pago.getMotivoCancelacion());
        assertNull(pago.getMotivoDevolucion());
    }

    @Test
    void testPagoDevuelto() {
        Pago pago = new Pago();
        pago.setEstado("DEVUELTO");
        pago.setMotivoDevolucion("Producto defectuoso");

        assertEquals("DEVUELTO", pago.getEstado());
        assertEquals("Producto defectuoso", pago.getMotivoDevolucion());
        assertNull(pago.getMotivoCancelacion());
    }
}