package api_oficina.com.example.oficina_api_demo.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import api_oficina.com.example.oficina_api_demo.businessprocess.OficinaBusinessProcess;

@ExtendWith(MockitoExtension.class)
class OficinaControllerTest {

	@Mock
	private OficinaBusinessProcess oficinaBusinessProcess;

	private MockMvc mockMvc;

	@BeforeEach
	void configurarMockMvc() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OficinaController(oficinaBusinessProcess)).build();
	}

	@Test
	void listaParticipantesEmJson() throws Exception {
		when(oficinaBusinessProcess.listarParticipantes()).thenReturn(List.of(Map.of(
				"id", 1L,
				"nome", "Ana",
				"email", "ana@example.com")));

		mockMvc.perform(get("/api/participantes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].nome").value("Ana"))
				.andExpect(jsonPath("$[0].email").value("ana@example.com"));

		verify(oficinaBusinessProcess).listarParticipantes();
	}

	@Test
	void listaOficinasEmJson() throws Exception {
		when(oficinaBusinessProcess.listarOficinas()).thenReturn(List.of(Map.of(
				"id", 1L,
				"titulo", "Programacao",
				"capacidade", 20,
				"vagas_disponiveis", 20)));

		mockMvc.perform(get("/api/oficinas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].titulo").value("Programacao"))
				.andExpect(jsonPath("$[0].capacidade").value(20))
				.andExpect(jsonPath("$[0].vagas_disponiveis").value(20));

		verify(oficinaBusinessProcess).listarOficinas();
	}

	@Test
	void listaInscricoesEmJson() throws Exception {
		when(oficinaBusinessProcess.listarInscricoes()).thenReturn(List.of(Map.of(
				"id", 1L,
				"participante_id", 1L,
				"oficina_id", 1L,
				"situacao", "ATIVA")));

		mockMvc.perform(get("/api/inscricoes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].participante_id").value(1))
				.andExpect(jsonPath("$[0].oficina_id").value(1))
				.andExpect(jsonPath("$[0].situacao").value("ATIVA"));

		verify(oficinaBusinessProcess).listarInscricoes();
	}
	@Test
	void buscaOficinaExistentePorId() throws Exception {
	    when(oficinaBusinessProcess.buscarOficinaPorId(1L))
	            .thenReturn(List.of(Map.of(
	                    "id", 1L,
	                    "titulo", "Introducao a Java",
	                    "capacidade", 3,
	                    "vagas_disponiveis", 3)));

	    mockMvc.perform(get("/api/oficinas/1"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.id").value(1))
	            .andExpect(jsonPath("$.titulo").value("Introducao a Java"))
	            .andExpect(jsonPath("$.capacidade").value(3))
	            .andExpect(jsonPath("$.vagas_disponiveis").value(3));

	    verify(oficinaBusinessProcess).buscarOficinaPorId(1L);
	}

	@Test
	void retorna404ParaOficinaInexistente() throws Exception {
	    when(oficinaBusinessProcess.buscarOficinaPorId(999999L))
	            .thenReturn(List.of());

	    mockMvc.perform(get("/api/oficinas/999999"))
	            .andExpect(status().isNotFound());

	    verify(oficinaBusinessProcess).buscarOficinaPorId(999999L);
	}
}
