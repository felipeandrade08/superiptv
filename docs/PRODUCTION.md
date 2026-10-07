# Produção do SuperIPTV

## Ordem de implantação

1. Provisione PostgreSQL e configure `DATABASE_URL`.
2. Configure `NODE_ENV=production`, `JWT_SECRET` com segredo aleatório longo, `MASTER_BOOTSTRAP_TOKEN` e `CORS_ORIGINS`.
3. Execute `npm --workspace backend/api run db:migrate` antes de iniciar uma nova versão da API.
4. Inicie a API com `npm --workspace backend/api run start`.
5. O processo só passa a escutar HTTP depois de confirmar conectividade com PostgreSQL.

## Migrations

`db:migrate` aplica os arquivos `backend/api/sql/*.sql` em ordem, dentro de transações. Cada migration aplicada fica registrada em `schema_migrations` com SHA-256. Um arquivo já aplicado não pode ser alterado silenciosamente: divergência de checksum interrompe o deploy.

Faça backup do banco antes de migrations destrutivas. Não execute migrations concorrentes em duas instâncias durante o mesmo deploy.

## Borda HTTP

A API usa allowlist de CORS via `CORS_ORIGINS` e limites específicos para bootstrap, login e refresh. Se estiver atrás de um proxy reverso confiável, configure `TRUST_PROXY=true`; não habilite isso quando clientes alcançarem diretamente o processo Node.

TLS/HTTPS deve terminar no proxy/plataforma de hospedagem. Os aplicativos de produção devem apontar somente para a URL HTTPS da API.

## Conteúdo e playback

O catálogo continua sendo provisionado exclusivamente pelo Master a partir de fonte que ele esteja autorizado a disponibilizar. O ticket de playback é curto e não contém a URL upstream, mas o endpoint atualmente responde com redirect para a origem. Portanto, a URL final pode ser observada por um cliente que inspecione o tráfego.

Um relay/proxy de mídia só deve ser adicionado se a licença/autorização da origem permitir e depois de dimensionar banda, conexões simultâneas e custo. Não trate o redirect atual como ocultação completa da origem.

## Segredos

Nunca versione `DATABASE_URL`, `JWT_SECRET`, `MASTER_BOOTSTRAP_TOKEN` ou credenciais da fonte. O token de bootstrap existe apenas para criar o primeiro Master e deve ser rotacionado/desabilitado operacionalmente depois do bootstrap.
