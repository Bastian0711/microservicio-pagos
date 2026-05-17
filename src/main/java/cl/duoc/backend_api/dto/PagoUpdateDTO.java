package cl.duoc.backend_api.dto;

import lombok.Data;

@Data
public class PagoUpdateDTO {

    private String metodoPago;
    private String estado;
}