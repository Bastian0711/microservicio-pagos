package cl.duoc.backend_api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.duoc.backend_api.client.PedidoClient;
import cl.duoc.backend_api.client.SubPedidoClient;
import cl.duoc.backend_api.dto.GenerarSubPedidoDTO;
import cl.duoc.backend_api.dto.PagoCreateDTO;
import cl.duoc.backend_api.dto.PagoDTO;
import cl.duoc.backend_api.dto.PagoUpdateDTO;
import cl.duoc.backend_api.dto.PedidoDTO;
import cl.duoc.backend_api.exception.EstadoInvalidoException;
import cl.duoc.backend_api.exception.RecursoNoEncontradoException;
import cl.duoc.backend_api.exception.ServicioNoDisponibleException;
import cl.duoc.backend_api.model.Pago;
import cl.duoc.backend_api.repository.PagoRepository;
import feign.FeignException;

@Service
public class PagoService {

    private static final Logger log =
            LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository repository;
    private final PedidoClient pedidoClient;
    private final SubPedidoClient subPedidoClient;

    public PagoService(
            PagoRepository repository,
            PedidoClient pedidoClient,
            SubPedidoClient subPedidoClient) {

        this.repository = repository;
        this.pedidoClient = pedidoClient;
        this.subPedidoClient = subPedidoClient;
    }

    public List<PagoDTO> listarPagos() {

        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PagoDTO obtenerPorId(Long id) {
        return toDto(obtenerEntidadPorId(id));
    }

    public PagoDTO procesarPago(PagoCreateDTO dto) {

        PedidoDTO pedido = validarPedido(dto.getIdPedido());

        if (!pedido.getEstado().equalsIgnoreCase("CONFIRMADO")) {

            throw new EstadoInvalidoException(
                    "El pedido no está confirmado y no permite procesar el pago");
        }

        log.info("Procesando pago para pedido id={}, total={}",
                pedido.getIdPedido(), pedido.getTotal());

        Pago pago = new Pago();

        pago.setIdPedido(pedido.getIdPedido());
        pago.setNombreCliente("Cliente " + pedido.getIdUsuario());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setMonto(pedido.getTotal());
        pago.setEstado("APROBADO");

        Pago guardado = repository.save(pago);

        log.info("Pago creado exitosamente id={}", guardado.getId());

        generarSubPedido(new GenerarSubPedidoDTO(pedido.getIdPedido()));

        return toDto(guardado);
    }

    public PagoDTO actualizarPago(Long id, PagoUpdateDTO dto) {

        if (dto == null) {
            dto = new PagoUpdateDTO();
        }

        Pago pago = obtenerEntidadPorId(id);

        if (dto.getMetodoPago() != null && !dto.getMetodoPago().isBlank()) {
            pago.setMetodoPago(dto.getMetodoPago());
        }

        return toDto(repository.save(pago));
    }

    public PagoDTO cancelarPago(Long id, String motivo) {

        if (motivo == null || motivo.isBlank()) {
            throw new EstadoInvalidoException("Debe ingresar un motivo de cancelación");
        }

        Pago pago = obtenerEntidadPorId(id);

        if ("CANCELADO".equalsIgnoreCase(pago.getEstado())) {
            throw new EstadoInvalidoException("El pago ya está cancelado");
        }

        pago.setEstado("CANCELADO");
        pago.setMotivoCancelacion(motivo);
        pago.setMotivoDevolucion(null);

        log.info("Pago id={} cancelado. Motivo: {}", id, motivo);

        return toDto(repository.save(pago));
    }

    public PagoDTO devolverPago(Long id, String motivo) {

        if (motivo == null || motivo.isBlank()) {
            throw new EstadoInvalidoException("Debe ingresar un motivo de devolución");
        }

        Pago pago = obtenerEntidadPorId(id);

        if (!"APROBADO".equalsIgnoreCase(pago.getEstado())) {
            throw new EstadoInvalidoException(
                    "Solo se pueden devolver pagos en estado APROBADO");
        }

        pago.setEstado("DEVUELTO");
        pago.setMotivoDevolucion(motivo);
        pago.setMotivoCancelacion(null);

        log.info("Pago id={} devuelto. Motivo: {}", id, motivo);

        return toDto(repository.save(pago));
    }

    public void eliminarPago(Long id) {

        Pago pago = obtenerEntidadPorId(id);

        repository.delete(pago);

        log.info("Pago eliminado id={}", id);
    }

    private PedidoDTO validarPedido(Long idPedido) {

        try {

            log.info("Consultando pedido id={}", idPedido);

            PedidoDTO pedido = pedidoClient.obtenerPedido(idPedido);

            log.info("Pedido encontrado: estado={}", pedido.getEstado());

            return pedido;

        } catch (FeignException.NotFound e) {

            log.warn("Pedido id={} no existe", idPedido);

            throw new RecursoNoEncontradoException("Pedido no encontrado");

        } catch (FeignException e) {

            log.error("Error al consultar servicio Pedidos: {}", e.getMessage());

            throw new ServicioNoDisponibleException(
                    "Servicio de pedidos no disponible");
        }
    }

    private void generarSubPedido(GenerarSubPedidoDTO dto) {

        try {

            log.info("Generando subpedidos para pedido id={}", dto.getIdPedido());

            subPedidoClient.generarSubpedidos(dto);

        } catch (FeignException.NotFound e) {

            throw new RecursoNoEncontradoException(
                    "No se pudo generar subpedidos: pedido no encontrado");

        } catch (FeignException e) {

            log.error("Error al consultar servicio SubPedidos: {}", e.getMessage());

            throw new ServicioNoDisponibleException(
                    "Servicio de subpedidos no disponible");
        }
    }

    private Pago obtenerEntidadPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Pago no encontrado"));
    }

    private PagoDTO toDto(Pago p) {

        return new PagoDTO(
                p.getId(),
                p.getIdPedido(),
                p.getNombreCliente(),
                p.getMetodoPago(),
                p.getMonto(),
                p.getEstado(),
                p.getMotivoCancelacion(),
                p.getMotivoDevolucion()
        );
    }
}
