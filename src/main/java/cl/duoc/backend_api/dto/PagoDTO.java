package cl.duoc.backend_api.dto;

import lombok.Data;

@Data
public class PagoDTO {

    private Long id;
    private Long idPedido;
    private String nombreCliente;
    private String metodoPago;
    private Double monto;
    private String estado;
}