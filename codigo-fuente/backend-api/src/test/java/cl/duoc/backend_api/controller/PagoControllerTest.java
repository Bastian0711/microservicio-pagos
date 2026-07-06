package cl.duoc.backend_api.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.backend_api.dto.PagoDTO;
import cl.duoc.backend_api.service.PagoService;

class PagoControllerTest {

    private final PagoService pagoService = mock(PagoService.class);
    private final PagoController pagoController = new PagoController(pagoService);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListarPagosRetorna200() throws Exception {
        PagoDTO dto = new PagoDTO();
        dto.setId(1L);
        dto.setEstado("APROBADO");

        when(pagoService.listarPagos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v3/pagos"))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarPorIdRetorna200() throws Exception {
        PagoDTO dto = new PagoDTO();
        dto.setId(1L);

        when(pagoService.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v3/pagos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarPagoRetorna200() throws Exception {
        doNothing().when(pagoService).eliminarPago(1L);

        mockMvc.perform(delete("/api/v3/pagos/1"))
                .andExpect(status().isOk());
    }
}