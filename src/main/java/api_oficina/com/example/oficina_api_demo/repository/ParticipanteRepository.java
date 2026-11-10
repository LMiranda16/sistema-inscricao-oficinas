package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipanteRepository {

	private final JdbcTemplate jdbcTemplate;

	public ParticipanteRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> listarTodos() {
		return jdbcTemplate.queryForList("SELECT * FROM participantes");
	}

	public Optional<Map<String, Object>> buscarPorId(Long id) {
		return jdbcTemplate.queryForList("SELECT * FROM participantes WHERE id = ?", id)
				.stream()
				.findFirst();
	}

	public Map<String, Object> adicionar(String nome, String email) {
		return jdbcTemplate.queryForMap(
				"INSERT INTO participantes (nome, email) VALUES (?, ?) RETURNING *",
				nome, email);
	}

	public boolean excluir(Long id) {
		return jdbcTemplate.update("DELETE FROM participantes WHERE id = ?", id) > 0;
	}
}
