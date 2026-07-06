package cl.duoc.backend_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.duoc.backend_api.dto.GenerarSubPedidoDTO;

@FeignClient(name = "subpedido-service", url = "${subpedido.service.url}")
public interface SubPedidoClient {

    @PostMapping("/api/v3/subpedidos/generar")
    void generarSubpedidos(@RequestBody GenerarSubPedidoDTO dto);
}