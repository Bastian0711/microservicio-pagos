package cl.duoc.backend_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.backend_api.dto.PagoCreateDTO;
import cl.duoc.backend_api.dto.PagoDTO;
import cl.duoc.backend_api.model.Pago;
import cl.duoc.backend_api.service.PagoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listarPagos() {
        return pagoService.listarPagos();
    }

    @GetMapping("/{id}")
    public Pago buscarPago(@PathVariable Long id) {
        return pagoService.buscarPorId(id);
    }

    @PostMapping
    public Pago crearPago(@Valid @RequestBody Pago pago) {
        return pagoService.guardarPago(pago);
    }

    @PostMapping("/procesar")
    public PagoDTO procesarPago(@RequestBody PagoCreateDTO dto) {
        return pagoService.procesarPago(dto);
    }

    @PutMapping("/{id}")
    public Pago actualizarPago(@PathVariable Long id, @Valid @RequestBody Pago pago) {
        return pagoService.actualizarPago(id, pago);
    }

    @PutMapping("/{id}/cancelar")
    public Pago cancelarPago(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return pagoService.cancelarPago(id, body.get("motivo"));
    }

    @PutMapping("/{id}/devolver")
    public Pago devolverPago(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return pagoService.devolverPago(id, body.get("motivo"));
    }

    @DeleteMapping("/{id}")
    public String eliminarPago(@PathVariable Long id) {
        boolean eliminado = pagoService.eliminarPago(id);

        if (eliminado) {
            return "Pago eliminado correctamente";
        } else {
            return "Pago no encontrado";
        }
    }
}
