package cl.duoc.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubPedidoDTO {

    private Long id;
    private Long pedidoId;
    private Long standId;
    private String descripcion;
    private String estado;
}
