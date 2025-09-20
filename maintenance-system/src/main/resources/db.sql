INSERT INTO manutency 
(descricao_servico, created_os, deadline_os, finished_os, status, prioridade, nome_solicitante, local)
VALUES
('Troca de lâmpada no corredor', TIMESTAMP '2025-09-15 09:00:00', DATE '2025-09-20', DATE '2025-09-16', 'Concluída', 'Média', 'Ana Souza', 'Bloco A - 2º andar'),

('Reparo em pia com vazamento', TIMESTAMP '2025-09-14 14:30:00', DATE '2025-09-19', NULL, 'Em andamento', 'Alta', 'Carlos Silva', 'Bloco B - Banheiro 3º andar'),

('Manutenção preventiva no ar-condicionado', TIMESTAMP '2025-09-13 08:15:00', DATE '2025-09-25', NULL, 'Aberta', 'Alta', 'Departamento de TI', 'Bloco C - Sala 305'),

('Pintura da sala de reuniões', TIMESTAMP '2025-09-12 10:00:00', DATE '2025-09-30', NULL, 'Aberta', 'Baixa', 'Diretoria', 'Bloco D - Sala 101');
