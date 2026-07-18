# Verificar o app (Invest&Multiply, JavaFX)

## Rodar o app
```bash
./gradlew run        # abre a janela JavaFX (mainClass projeto.gui.MainApp)
```
Compilar apenas: `./gradlew compileJava`.

## Dirigir a GUI programaticamente (verificação end-to-end)
Não há TestFX. O que funciona: um driver Java que chama
`Application.launch(MainApp.class)` numa thread e dirige a árvore de cena via
`Platform.runLater` — preenche `TextField`s (localizados por promptText),
dispara `Button.fire()` (localizados pelo texto) e trata diálogos.

Pontos-chave do driver (exemplo funcional já usado: `DriverGui.java`, sessão de 2026-07-17):
- Diálogos usam `showAndWait` (event loop aninhado): agende o handler do diálogo
  com `Platform.runLater` ANTES de `fire()` no botão que o abre; o handler
  procura em `Window.getWindows()` uma janela cuja raiz é `DialogPane` e se
  reagenda até encontrar. Confirmar com `dialogPane.lookupButton(ButtonType.OK)`.
- Screenshots sem javafx.swing: `node.snapshot(...)` → copiar pixels com
  `PixelReader.getArgb` para `BufferedImage` → `ImageIO.write`.
- Classpath/module-path: jars do JavaFX (mac-aarch64) e dependências ficam em
  `~/.gradle/caches/modules-2/files-2.1/`; JDK 21 em `~/.gradle/jdks/`.
  Pegue a linha de comando exata com `ps` enquanto `./gradlew run` está aberto.

## Isolamento de dados (importante)
- Usuários são salvos em `data/usuario_<nome>.json` **relativo ao cwd** —
  rode o driver com cwd num diretório temporário para não poluir dados reais.
- A carteira só é salva ao clicar "Salvar".
- O "Lembrar-me" usa `java.util.prefs.Preferences` (chave `usuario_lembrado`,
  nó de `projeto.gui.TelaLogin`) — é global do sistema, não isolável por cwd.
  Limpe depois do teste, sabendo que apaga também o valor real do usuário.

## Fluxos que valem dirigir
1. Criar conta → cadastrar Ação (PETR4) e Criptomoeda (bitcoin) → conferir
   preço por cota na tabela (cripto testa o parse do CoinMarketCap: valores
   com vírgula de milhar, ex. "$63,143.59").
2. Comprar/Vender sobre a linha selecionada; probe: vender mais do que possui,
   comprar valor não numérico (devem dar Alert de erro, não travar).
3. Salvar → relançar o app → login de novo → tabela recarregada (exercita o
   desserializador por `tipo` do GsonFactory).

## Pegadinhas
- Scraping roda na FX thread (janela congela alguns segundos ao cadastrar).
- Fundamentus exige User-Agent de navegador via curl, mas o UA padrão do Jsoup passa.
- FundoDeInvestimento e TituloRendaFixa não têm fonte de preço: preço 0 e
  quantidade infinita ao cadastrar (sem guarda de divisão por zero).
