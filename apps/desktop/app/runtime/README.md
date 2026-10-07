# VLC runtime for SuperIPTV Desktop

O player Desktop usa VLCJ e procura um runtime VLC em `vlc/` ao lado dos recursos empacotados.

Antes de uma distribuição oficial, coloque aqui uma distribuição Windows x64 do VLC/libVLC cuja redistribuição esteja aprovada para o projeto, preservando pelo menos:

- `libvlc.dll`
- `libvlccore.dll`
- diretório `plugins/`

Não versionar binários de terceiros sem registrar origem, versão e licença. A tarefa Gradle `verifyVlcRuntime` valida a estrutura antes do pacote oficial.
