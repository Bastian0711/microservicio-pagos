package cl.duoc.backend_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.backend_api.client.PedidoClient;
import cl.duoc.backend_api.client.SubPedidoClient;
import cl.duoc.backend_api.dto.GenerarSubPedidoDTO;
import cl.duoc.backend_api.dto.PagoCreateDTO;
import cl.duoc.backend_api.dto.PagoDTO;
import cl.duoc.backend_api.dto.PedidoDTO;
import cl.duoc.backend_api.exception.RecursoNoEncontradoException;
import cl.duoc.backend_api.exception.ServicioNoDisponibleException;
import cl.duoc.backend_api.model.Pago;
import cl.duoc.backend_api.repository.PagoRepository;
import feign.FeignException;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoClient pedidoClient;
    private final SubPedidoClient subPedidoClient;

    public PagoService(PagoRepository pagoRepository,
            PedidoClient pedidoClient,
            SubPedidoClient subPedidoClient) {
        this.pagoRepository = pagoRepository;
        this.pedidoClient = pedidoClient;
        this.subPedidoClient = subPedidoClient;
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

    public PagoDTO procesarPago(PagoCreateDTO dto) {

        PedidoDTO pedido = validarPedido(dto.getIdPedido());

        if (!pedido.getEstado().equalsIgnoreCase("CONFIRMADO")) {
            throw new RuntimeException("El pedido no está confirmado");
        }

        Pago pago = new Pago();
        pago.setIdPedido(pedido.getIdPedido());
        pago.setNombreCliente("Cliente " + pedido.getIdUsuario());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setMonto(pedido.getTotal());
        pago.setEstado("APROBADO");
        pago.setMotivoCancelacion(null);
        pago.setMotivoDevolucion(null);

        Pago pagoGuardado = pagoRepository.save(pago);

        GenerarSubPedidoDTO generarSubPedidoDTO = new GenerarSubPedidoDTO();
        generarSubPedidoDTO.setIdPedido(pedido.getIdPedido());

        generarSubPedido(generarSubPedidoDTO);

        PagoDTO response = new PagoDTO();
        response.setId(pagoGuardado.getId());
        response.setIdPedido(pagoGuardado.getIdPedido());
        response.setNombreCliente(pagoGuardado.getNombreCliente());
        response.setMetodoPago(pagoGuardado.getMetodoPago());
        response.setMonto(pagoGuardado.getMonto());
        response.setEstado(pagoGuardado.getEstado());

        return response;
    }

    private PedidoDTO validarPedido(Long idPedido) {
        try {
            PedidoDTO pedido = pedidoClient.obtenerPedido(idPedido);

            if (pedido == null) {
                throw new RecursoNoEncontradoException("Pedido no encontrado");
            }

            return pedido;

        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Pedido no encontrado");

        } catch (FeignException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo consultar el microservicio de pedidos");
        }
    }

    private void generarSubPedido(GenerarSubPedidoDTO dto) {
        try {
            subPedidoClient.generarSubpedidos(dto);

        } catch (FeignException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo consultar el microservicio de subpedidos");
        }
    }
}