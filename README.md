# SwiftKey Clean Patches

Patches incrementais para **Microsoft SwiftKey 9.13.19.3** (`com.touchtype.swiftkey`,
version code `1236467760`). Projeto independente, sem vínculo com Microsoft ou Morphe.

| Patch | Resultado |
|---|---|
| Disable cloud sign-in prompt | Desativa as decisões de convite automático ao login na tela inicial. Configuração local e login manual continuam disponíveis. |
| Disable telemetry | Interrompe os registros e upload de telemetria identificados, Adjust e a coleta descrita no patch original. Não representa uma auditoria de todo o tráfego de rede do app. |
| Customise toolbar | Adiciona **Personalizar toolbar** às configurações do SwiftKey para selecionar e ordenar os atalhos. |

Os dois primeiros patches foram adaptados do [Hooman](https://github.com/arandomhooman/hoomans-morphe-patches).
Veja [atribuição e licenças](THIRD_PARTY_NOTICES.md).

## Toolbar

1. Abra as configurações do SwiftKey e toque em **Personalizar toolbar**, logo abaixo de **Layout e teclas**.
2. Marque somente os atalhos desejados e use ↑ / ↓ para ordenar.
3. Toque em **Salvar alterações**.

As escolhas ficam no aparelho. A montagem nativa da toolbar distribui os itens
visíveis, sem reservar espaços para os ocultos. **Mais opções** permanece acessível;
é necessário pelo menos um outro atalho para o layout nativo. Itens indisponíveis
naquele contexto são ignorados; Configurações serve como fallback quando possível.
O número exibido se adapta à largura. A lista original de **Mais opções** permanece
disponível. **Restaurar padrão do SwiftKey** devolve o controle ao app.

A personalização começa desligada, preservando a toolbar original até você salvar
suas escolhas. O editor usa tema claro/escuro e não lê o texto digitado.

## Estado da primeira versão

**Experimental.** Os três patches foram aplicados ao APKM 9.13.19.3 com
validação dos fingerprints, reconstrução e assinatura do APK. Os testes de seleção
e da tela de edição rodam localmente. O teclado completo e o alinhamento visual
em aparelho real ainda precisam de validação; aplicar o patch não demonstra
compatibilidade de todos os recursos do SwiftKey.

O APK modificado tem outra assinatura. Não substitui normalmente uma instalação
oficial com a mesma identificação. Planeje a migração dos dados e o teste antes
de remover a instalação existente; este projeto não desinstala nem instala apps.

## Compilar

Requisitos: JDK 21+, Python 3.11+, `curl` e Android SDK com
`platforms;android-36`. Configure `JAVA_HOME` e `ANDROID_HOME` conforme seu ambiente.
No Mac com Homebrew, os scripts reconhecem o JDK 21 instalado pelo Homebrew.

```sh
chmod +x gradlew scripts/*.sh
./scripts/build.sh
```

As ferramentas públicas são baixadas com versões e hashes fixos.
**Nenhum token do GitHub é necessário para compilar.** O build usa as APIs do
Patcher 2.3.0 incluídas no Desktop 2.1.1 como dependência somente de compilação.
O runtime do Desktop não é empacotado no bundle.

Saída: `patches/build/libs/swiftkey-clean-patches-0.1.0.mpp`, com classes JVM e DEX
para os carregadores do Desktop e Android. A extensão do teclado contém apenas DEX.

## Aplicar ao seu APK ou APKM original

```sh
./scripts/patch.sh /caminho/swiftkey-original.apkm
```

Saída padrão: `artifacts/swiftkey-clean-9.13.19.3.apk` e relatório JSON em
`artifacts/patch-result.json`. O script não instala o resultado.
Use o arquivo original a cada build; não aplique sucessivamente sobre APKs já modificados.
Mantenha a chave local usada pelo Desktop para assinar as próximas atualizações.

Você também pode carregar o `.mpp` no Morphe Desktop. Esta primeira versão foi
validada com Desktop 2.1.1; a execução pelo Manager no Android ainda não foi testada.

## Evolução

Os patches são independentes. As mudanças ficam em `patches/`; o editor e suas
regras ficam em `extensions/swiftkey/`. A ponte para as classes ofuscadas é gerada
ao aplicar o patch, sem empacotar classes falsas do app. Padrões ambíguos interrompem
a aplicação em vez de selecionar arbitrariamente um método.

Para atualizar o SwiftKey, analise o novo APK, ajuste os fingerprints e o alvo,
rode os testes e aplique todos os patches desejados ao novo original. A compatibilidade
com outras versões, incluindo a 9.13.13.5, não é declarada neste bundle.

Licença: [GPL-3.0](LICENSE).
