package cl.duoc.backend_api.service;

import cl.duoc.backend_api.model.Pago;
import cl.duoc.backend_api.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public Pago guardarPago(Pago pago) {
        pago.setEstado("PENDIENTE");
        pago.setMotivoCancelacion(null);
        pago.setMotivoDevolucion(null);

        return pagoRepository.save(pago);
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago actualizarPago(Long id, Pago pagoActualizado) {
        Pago pago = buscarPorId(id);

        if (pago == null) {
            return null;
        }

        pago.setNombreCliente(pagoActualizado.getNombreCliente());
        pago.setMetodoPago(pagoActualizado.getMetodoPago());
        pago.setMonto(pagoActualizado.getMonto());
        pago.setEstado(pagoActualizado.getEstado());

        return pagoRepository.save(pago);
    }

    public Pago cancelarPago(Long id, String motivo) {
        Pago pago = buscarPorId(id);

        if (pago == null) {
            return null;
        }

        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("Debe ingresar un motivo de cancelación");
        }

        pago.setEstado("CANCELADO");
        pago.setMotivoCancelacion(motivo);
        pago.setMotivoDevolucion(null);

        return pagoRepository.save(pago);
    }

    public Pago devolverPago(Long id, String motivo) {
        Pago pago = buscarPorId(id);

        if (pago == null) {
            return null;
        }

        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("Debe ingresar un motivo de devolución");
        }

        pago.setEstado("DEVUELTO");
        pago.setMotivoDevolucion(motivo);
        pago.setMotivoCancelacion(null);

        return pagoRepository.save(pago);
    }

    public boolean eliminarPago(Long id) {
        Pago pago = buscarPorId(id);

        if (pago == null) {
            return false;
        }

        pagoRepository.delete(pago);
        return true;
    }
}