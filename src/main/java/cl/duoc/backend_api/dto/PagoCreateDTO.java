package cl.duoc.backend_api.dto;

import lombok.Data;

@Data
public class PagoCreateDTO {

    private Long idPedido;
    private String metodoPago;
}