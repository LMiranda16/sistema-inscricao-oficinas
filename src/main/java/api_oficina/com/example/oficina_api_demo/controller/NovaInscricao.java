package api_oficina.com.example.oficina_api_demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NovaInscricao(
		@JsonProperty("participante_id") Long participanteId,
		@JsonProperty("oficina_id") Long oficinaId) {
}
