package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;

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
	
	public boolean existePorId(Long id) {
	    String sql = "SELECT COUNT(*) FROM participantes WHERE id = ?";

	    Integer quantidade = jdbcTemplate.queryForObject(sql, Integer.class, id);

	    return quantidade != null && quantidade > 0;
	}
}
