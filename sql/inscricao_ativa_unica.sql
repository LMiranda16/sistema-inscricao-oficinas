-- Verifique e resolva duplicatas existentes antes de executar esta migracao.
CREATE UNIQUE INDEX IF NOT EXISTS inscricoes_ativa_unica
    ON inscricoes (participante_id, oficina_id)
    WHERE situacao = 'ATIVA';
