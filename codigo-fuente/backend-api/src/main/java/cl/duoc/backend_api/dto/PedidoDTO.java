package cl.duoc.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long idPedido;
    private Double total;
    private String estado;
    private Long idUsuario;
    private Long idEvento;
}
