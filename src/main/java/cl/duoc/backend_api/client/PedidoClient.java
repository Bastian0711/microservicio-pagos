package cl.duoc.backend_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.backend_api.dto.PedidoDTO;

@FeignClient(name = "pedido-service", url = "${pedido.service.url}")
public interface PedidoClient {

    @GetMapping("/api/pedidos/{id}")
    PedidoDTO obtenerPedido(@PathVariable Long id);
}