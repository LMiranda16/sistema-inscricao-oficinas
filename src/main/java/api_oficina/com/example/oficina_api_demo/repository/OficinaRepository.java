package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OficinaRepository {

	private final JdbcTemplate jdbcTemplate;

	public OficinaRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> listarTodos() {
		return jdbcTemplate.queryForList("SELECT * FROM oficinas");
	}

	public Optional<Map<String, Object>> buscarPorId(Long id) {
		return jdbcTemplate.queryForList("SELECT * FROM oficinas WHERE id = ?", id)
				.stream()
				.findFirst();
	}

	public Optional<Map<String, Object>> buscarPorIdComBloqueio(Long id) {
		return jdbcTemplate.queryForList("SELECT * FROM oficinas WHERE id = ? FOR UPDATE", id)
				.stream().findFirst();
	}

	public boolean temHistorico(Long id) {
		return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
				"SELECT EXISTS (SELECT 1 FROM inscricoes WHERE oficina_id = ?)", Boolean.class, id));
	}

	public Map<String, Object> atualizar(Long id, String titulo, int capacidade, int vagas) {
		return jdbcTemplate.queryForMap("UPDATE oficinas SET titulo = ?, capacidade = ?, vagas_disponiveis = ? WHERE id = ? RETURNING *",
				titulo, capacidade, vagas, id);
	}

	public Map<String, Object> adicionar(String titulo, int capacidade) {
		return jdbcTemplate.queryForMap(
				"INSERT INTO oficinas (titulo, capacidade, vagas_disponiveis) VALUES (?, ?, ?) RETURNING *",
				titulo, capacidade, capacidade);
	}

	public boolean excluir(Long id) {
		return jdbcTemplate.update("DELETE FROM oficinas WHERE id = ?", id) > 0;
	}

	public boolean consumirVaga(Long id) {
		return jdbcTemplate.update(
				"UPDATE oficinas SET vagas_disponiveis = vagas_disponiveis - 1 "
						+ "WHERE id = ? AND vagas_disponiveis > 0",
				id) > 0;
	}

	public void devolverVaga(Long id) {
		jdbcTemplate.update(
				"UPDATE oficinas SET vagas_disponiveis = vagas_disponiveis + 1 WHERE id = ?",
				id);
	}
}
