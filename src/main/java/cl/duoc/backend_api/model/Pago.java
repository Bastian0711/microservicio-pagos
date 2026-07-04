package cl.duoc.backend_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "metodo_pago")
    private String metodoPago;

    private Double monto;

    private String estado;

    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;

    @Column(name = "motivo_devolucion")
    private String motivoDevolucion;
}
