# Arquitetura do SuperIPTV 2.0

## Visão

O projeto será construído como monorepo. O aplicativo Android permanece nativo em Kotlin por ser a melhor base para reprodução com Media3, Android TV, D-pad, PiP e integração com o sistema.

## Camadas Android

- presentation: Compose, navegação, estado de tela
- domain: casos de uso e regras
- data: repositórios, Room/DataStore, rede
- player: Media3, sessão, áudio, legenda, reconexão
- playlist: M3U/M3U8, categorias e normalização
- epg: XMLTV e programação
- core: modelos compartilhados e utilitários

## Aplicativos

### Mobile
Experiência touch com navegação otimizada para celular/tablet, PiP e player em tela cheia.

### TV
UX independente orientada a foco, controle remoto/D-pad, telas 10-foot UI e reprodução contínua.

## Backend

Node.js + TypeScript com separação por módulos:

- auth
- users
- devices
- subscriptions
- plans
- sessions
- audit
- admin

O backend não será responsável por hospedar conteúdo IPTV do usuário.

## Painel administrativo

React + TypeScript + Vite.

Módulos previstos:

- Dashboard
- Usuários
- Planos
- Assinaturas
- Dispositivos
- Sessões
- Logs
- Configurações

## Roadmap inicial

### Pacote 1 — Fundação
Monorepo, documentação, CI e esqueletos das aplicações.

### Pacote 2 — Core Android
Tema, navegação, arquitetura de estado, banco local e DataStore.

### Pacote 3 — Playlist
Importação M3U, parser, categorias, favoritos e busca.

### Pacote 4 — Player
Media3, áudio, legenda, reconexão, histórico e continuar assistindo.

### Pacote 5 — TV
Navegação por D-pad, foco, home TV e player otimizado.

### Pacote 6 — Backend
Auth, usuários, dispositivos, planos e sessões.

### Pacote 7 — Admin
Dashboard e gerenciamento operacional.

### Pacote 8 — EPG e acabamento
XMLTV, guia de programação, performance, UX e preparação de release.
