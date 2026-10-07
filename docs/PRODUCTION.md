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

## Criptografia do catálogo

Configure `CATALOG_SECRET_KEY` antes de sincronizar catálogo em produção e mantenha essa chave em um cofre/secret manager. A API usa AES-256-GCM e não devolve a fonte privada nas rotas administrativas.

Para uma instalação que já possua catálogo em plaintext:

1. faça backup do PostgreSQL;
2. implante o código e execute `npm --workspace backend/api run db:migrate`;
3. com a mesma `CATALOG_SECRET_KEY` definitiva configurada, execute `npm --workspace backend/api run db:encrypt-catalog`;
4. valide login, catálogo e playback;
5. não troque a chave sem um processo explícito de rotação/recriptografia.

As colunas legadas permanecem temporariamente para compatibilidade de migração, mas dados novos são gravados apenas nos campos criptografados.

## Limites de sincronização

`CATALOG_SYNC_MAX_BYTES` limita o total recebido da fonte e `CATALOG_SYNC_MAX_ITEMS` limita a quantidade de itens processados. O sync tem timeout de conexão/resposta e aborta a transação se ultrapassar os limites; o catálogo ativo anterior permanece válido porque a alteração ocorre dentro de transação.

## Endpoints dos aplicativos

Android Mobile e Android TV recebem a API pelo Gradle property `SUPERIPTV_API_URL`. Em produção, forneça uma URL HTTPS durante o build. O Desktop lê `SUPERIPTV_API_URL` do ambiente. O painel web usa `VITE_API_URL`.

Os fallbacks HTTP/localhost existem somente para desenvolvimento local e não devem ser usados nos artefatos distribuídos.
