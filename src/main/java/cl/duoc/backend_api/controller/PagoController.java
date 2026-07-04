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
import cl.duoc.backend_api.dto.PagoUpdateDTO;
import cl.duoc.backend_api.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/pagos")
@Tag(name = "Pagos", description = "Operaciones relacionadas con pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Listar todos los pagos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<PagoDTO> listarPagos() {
        return pagoService.listarPagos();
    }

    @Operation(summary = "Buscar pago por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public PagoDTO buscarPago(@PathVariable Long id) {
        return pagoService.obtenerPorId(id);
    }

    @Operation(summary = "Crear un nuevo pago")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public PagoDTO crearPago(@Valid @RequestBody PagoCreateDTO dto) {
        return pagoService.procesarPago(dto);
    }

    @Operation(summary = "Actualizar un pago existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PutMapping("/{id}")
    public PagoDTO actualizarPago(@PathVariable Long id, @Valid @RequestBody PagoUpdateDTO dto) {
        return pagoService.actualizarPago(id, dto);
    }

    @Operation(summary = "Cancelar un pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago cancelado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PutMapping("/{id}/cancelar")
    public PagoDTO cancelarPago(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return pagoService.cancelarPago(id, body.get("motivo"));
    }

    @Operation(summary = "Devolver un pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago devuelto"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PutMapping("/{id}/devolver")
    public PagoDTO devolverPago(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return pagoService.devolverPago(id, body.get("motivo"));
    }

    @Operation(summary = "Eliminar un pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago eliminado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @DeleteMapping("/{id}")
    public String eliminarPago(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return "Pago eliminado correctamente";
    }
}