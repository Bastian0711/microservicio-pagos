package cl.duoc.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private Long idPedido;
    private String nombreCliente;
    private String metodoPago;
    private Double monto;
    private String estado;
    private String motivoCancelacion;
    private String motivoDevolucion;
}
