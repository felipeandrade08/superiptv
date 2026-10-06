# SuperIPTV

SuperIPTV é um player IPTV multiplataforma em evolução, com foco inicial em Android Mobile e Android TV.

> O projeto não fornece canais, filmes, séries ou listas IPTV. O usuário adiciona suas próprias fontes compatíveis.

## Arquitetura

- Android: Kotlin + Jetpack Compose + Media3
- Android TV: Kotlin + Compose for TV + Media3
- API: Node.js + TypeScript
- Banco: PostgreSQL
- Painel Admin: React + TypeScript + Vite
- CI/CD: GitHub Actions

## Estrutura

```text
apps/
  android/
    mobile/
    tv/
  admin/
backend/
  api/
docs/
.github/workflows/
```

## Objetivos da primeira fase

1. Fundação do monorepo.
2. Aplicativo Android compilando do zero.
3. Base compartilhada Mobile/TV.
4. Player Media3.
5. Importação de playlist M3U.
6. Persistência local.
7. API TypeScript.
8. Painel administrativo inicial.
9. CI verde no GitHub.

## Princípios

- Build reproduzível.
- Nada de arquivos obrigatórios criados manualmente após clonar.
- Separação clara entre UI, domínio e dados.
- Mobile e TV compartilham regra de negócio, mas têm UX própria.
- Credenciais e URLs sensíveis nunca ficam hardcoded no repositório.
- Mudanças entram em pacotes fechados com revisão e CI.

## Status

Projeto reiniciado do zero em outubro de 2026.
