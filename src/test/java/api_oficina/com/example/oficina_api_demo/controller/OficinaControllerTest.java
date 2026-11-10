package api_oficina.com.example.oficina_api_demo.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import api_oficina.com.example.oficina_api_demo.businessprocess.OficinaBusinessProcess;

@ExtendWith(MockitoExtension.class)
class OficinaControllerTest {

	@Mock
	private OficinaBusinessProcess oficinaBusinessProcess;

	private MockMvc mockMvc;

	@BeforeEach
	void configurarMockMvc() {
		mockMvc = MockMvcBuilders.standaloneSetup(new OficinaController(oficinaBusinessProcess))
				.setControllerAdvice(new ApiExceptionHandler())
				.build();
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
		when(oficinaBusinessProcess.listarOficinas()).thenReturn
		(List.of(Map.of(
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
	void rotasOrdenadasRetornamAsEntidadesOrdenadas() throws Exception {
		List<Map<String, Object>> participantes = List.of(Map.of("id", 1, "nome", "Ana"));
		List<Map<String, Object>> oficinas = List.of(Map.of("id", 2, "titulo", "Programacao"));
		List<Map<String, Object>> inscricoes = List.of(Map.of("id", 3, "situacao", "ATIVA"));
		when(oficinaBusinessProcess.listarParticipantesOrdenados()).thenReturn(participantes);
		when(oficinaBusinessProcess.listarOficinasOrdenadas()).thenReturn(oficinas);
		when(oficinaBusinessProcess.listarInscricoesOrdenadas()).thenReturn(inscricoes);

		mockMvc.perform(get("/api/participantes/ordenar"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
		mockMvc.perform(get("/api/oficinas/ordenar"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].titulo").value("Programacao"));
		mockMvc.perform(get("/api/inscricoes/ordenar"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].situacao").value("ATIVA"));

		verify(oficinaBusinessProcess).listarParticipantesOrdenados();
		verify(oficinaBusinessProcess).listarOficinasOrdenadas();
		verify(oficinaBusinessProcess).listarInscricoesOrdenadas();
	}

	@Test
	void buscaPorIdNasTresTabelas() throws Exception {
		when(oficinaBusinessProcess.buscarParticipante(1L)).thenReturn(Map.of("id", 1L, "nome", "Ana"));
		when(oficinaBusinessProcess.buscarOficina(1L)).thenReturn(Map.of("id", 1L, "titulo", "Programacao"));
		when(oficinaBusinessProcess.buscarInscricao(1L)).thenReturn(Map.of("id", 1L, "situacao", "ATIVA"));

		mockMvc.perform(get("/api/participantes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Ana"));
		mockMvc.perform(get("/api/oficinas/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.titulo").value("Programacao"));
		mockMvc.perform(get("/api/inscricoes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.situacao").value("ATIVA"));
	}

	@Test
	void buscaPorIdInexistenteRetorna404() throws Exception {
		when(oficinaBusinessProcess.buscarParticipante(999L))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
		when(oficinaBusinessProcess.buscarOficina(999L))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
		when(oficinaBusinessProcess.buscarInscricao(999L))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

		mockMvc.perform(get("/api/participantes/999")).andExpect(status().isNotFound());
		mockMvc.perform(get("/api/oficinas/999")).andExpect(status().isNotFound());
		mockMvc.perform(get("/api/inscricoes/999")).andExpect(status().isNotFound());
	}

	@Test
	void cadastraRegistrosNasTresTabelas() throws Exception {
		when(oficinaBusinessProcess.adicionarParticipante("Ana", "ana@example.com"))
				.thenReturn(Map.of("id", 1L, "nome", "Ana"));
		when(oficinaBusinessProcess.adicionarOficina("Programacao", 20))
				.thenReturn(Map.of("id", 1L, "titulo", "Programacao"));
		when(oficinaBusinessProcess.adicionarInscricao(1L, 1L))
				.thenReturn(Map.of(
						"id", 1L,
						"participante_id", 1L,
						"oficina_id", 1L,
						"situacao", "ATIVA"));

		mockMvc.perform(post("/api/participantes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana\",\"email\":\"ana@example.com\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));
		mockMvc.perform(post("/api/oficinas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"titulo\":\"Programacao\",\"capacidade\":20}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.titulo").value("Programacao"));
		mockMvc.perform(post("/api/inscricoes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"participante_id\":1,\"oficina_id\":1}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.participante_id").value(1))
				.andExpect(jsonPath("$.oficina_id").value(1))
				.andExpect(jsonPath("$.situacao").value("ATIVA"));

		verify(oficinaBusinessProcess).adicionarInscricao(1L, 1L);
	}

	@Test
	void cadastroComDadosInvalidosRetorna400() throws Exception {
		when(oficinaBusinessProcess.adicionarParticipante("", "ana@example.com"))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST));
		when(oficinaBusinessProcess.adicionarOficina("Programacao", 0))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST));
		when(oficinaBusinessProcess.adicionarInscricao(null, 1L))
				.thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST));

		mockMvc.perform(post("/api/participantes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"\",\"email\":\"ana@example.com\"}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/api/oficinas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"titulo\":\"Programacao\",\"capacidade\":0}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/api/inscricoes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"oficina_id\":1}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void emailDuplicadoRetorna409() throws Exception {
		doThrow(new DataIntegrityViolationException("email duplicado"))
				.when(oficinaBusinessProcess).adicionarParticipante("Ana", "ana@example.com");

		mockMvc.perform(post("/api/participantes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana\",\"email\":\"ana@example.com\"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.erro").exists());
	}

	@Test
	void removeParticipanteEOficinaECancelaInscricao() throws Exception {
		when(oficinaBusinessProcess.excluirInscricao(1L))
				.thenReturn(Map.of("id", 1L, "situacao", "CANCELADA"));

		mockMvc.perform(delete("/api/participantes/1"))
				.andExpect(status().isNoContent());
		mockMvc.perform(delete("/api/oficinas/1"))
				.andExpect(status().isNoContent());
		mockMvc.perform(delete("/api/inscricoes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.situacao").value("CANCELADA"));
	}
}
