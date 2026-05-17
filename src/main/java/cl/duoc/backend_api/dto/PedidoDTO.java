package cl.duoc.backend_api.dto;

import lombok.Data;

@Data
public class PedidoDTO {

    private Long idPedido;
    private Double total;
    private String estado;
    private Long idUsuario;
    private Long idEvento;
}