package api_oficina.com.example.oficina_api_demo.model;

import java.time.LocalDateTime;

public record Inscricao(Long id, Long participanteId, Long oficinaId,
                        LocalDateTime dataRegistro, String situacao) {
}
