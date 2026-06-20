package cl.duoc.backend_api.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.backend_api.client.PedidoClient;
import cl.duoc.backend_api.client.SubPedidoClient;
import cl.duoc.backend_api.dto.PagoDTO;
import cl.duoc.backend_api.exception.EstadoInvalidoException;
import cl.duoc.backend_api.exception.RecursoNoEncontradoException;
import cl.duoc.backend_api.model.Pago;
import cl.duoc.backend_api.repository.PagoRepository;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private SubPedidoClient subPedidoClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void testListarPagos() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setNombreCliente("Juan");
        pago.setMetodoPago("TARJETA");
        pago.setMonto(10000.0);
        pago.setEstado("APROBADO");

        when(repository.findAll()).thenReturn(List.of(pago));

        List<PagoDTO> resultado = pagoService.listarPagos();

        assertEquals(1, resultado.size());
        assertEquals("APROBADO", resultado.get(0).getEstado());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testObtenerPorIdExistente() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setNombreCliente("María");
        pago.setMetodoPago("EFECTIVO");
        pago.setMonto(5000.0);
        pago.setEstado("APROBADO");

        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        PagoDTO resultado = pagoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("María", resultado.getNombreCliente());
    }

    @Test
    void testObtenerPorIdNoExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pagoService.obtenerPorId(99L));
    }

    @Test
    void testCancelarPagoSinMotivo() {
        assertThrows(EstadoInvalidoException.class,
                () -> pagoService.cancelarPago(1L, ""));
    }

    @Test
    void testCancelarPagoConMotivo() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setEstado("APROBADO");
        pago.setNombreCliente("Juan");
        pago.setMetodoPago("TARJETA");
        pago.setMonto(10000.0);

        when(repository.findById(1L)).thenReturn(Optional.of(pago));
        when(repository.save(any(Pago.class))).thenReturn(pago);

        PagoDTO resultado = pagoService.cancelarPago(1L, "No quiero");

        assertEquals("CANCELADO", resultado.getEstado());
    }

    @Test
    void testEliminarPagoNoExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pagoService.eliminarPago(99L));
    }
}