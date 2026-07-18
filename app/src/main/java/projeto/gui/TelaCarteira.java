package projeto.gui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import projeto.excecoes.InvalidAssetException;
import projeto.excecoes.PersistenceException;
import projeto.investimentos.FinancialAsset;
import projeto.servicos.ReportService;
import projeto.servicos.UserController;
import projeto.sistemas.Carteira;

/**
 * Tela principal: lista os ativos da carteira, permite cadastrar, editar,
 * remover e atualizar ativos, gerar relatórios e visualizar o dashboard.
 */
public class TelaCarteira {

    private final MainApp app;
    private final UserController userController;
    private final Carteira carteira;
    private final ReportService reportService = new ReportService();

    private final ObservableList<FinancialAsset> dados = FXCollections.observableArrayList();
    private final TableView<FinancialAsset> tabela = new TableView<>();
    private final Label labelTotal = new Label();

    private static final String[] PALETA = {
            "#f3622d", "#fba71b", "#57b757", "#41a9c9", "#4258c9", "#9a42c8", "#c84164"
    };

    private final PieChart graficoPizza = new PieChart();
    private final StackedBarChart<String, Number> graficoBarras =
            new StackedBarChart<>(new CategoryAxis(), new NumberAxis());
    private final Label labelCentro = new Label();
    private final VBox legenda = new VBox(8);

    public TelaCarteira(MainApp app, UserController userController) {
        this.app = app;
        this.userController = userController;
        this.carteira = userController.getUsuarioAtual().getCarteira();
    }

    public Scene criarCena() {
        montarTabela();

        Label titulo = new Label("Carteira de " + userController.getUsuarioAtual().getNome());
        titulo.setMaxWidth(Double.MAX_VALUE);
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;"
                + " -fx-text-fill: #e1e6e9; -fx-background-color: #2a2a2a;"
                + " -fx-padding: 12 16 12 16;");

        TabPane abas = new TabPane();
        abas.getTabs().add(criarAbaAtivos());
        abas.getTabs().add(criarAbaDashboard());
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane raiz = new BorderPane();
        raiz.setTop(titulo);
        raiz.setCenter(abas);

        atualizar();
        Scene cena = new Scene(raiz, 820, 600);
        Recursos.aplicarTemaDark(cena);
        return cena;
    }

    // ------------------------------------------------------------------
    // Aba de ativos
    // ------------------------------------------------------------------

    private Tab criarAbaAtivos(){
        Button cadastrar = new Button("Cadastrar");
        Button comprar = new Button("Comprar");
        Button vender = new Button("Vender");
        Button editar = new Button("Editar");
        Button remover = new Button("Remover");
        Button atualizarPrecos = new Button("Atualizar preços");
        Button relGeral = new Button("Relatório geral");
        Button relTipo = new Button("Relatório por tipo");
        Button alertas = new Button("Alertas");
        Button salvar = new Button("Salvar");
        Button sair = new Button("Sair");

        cadastrar.setOnAction(e -> abrirDialogoCadastro());
        comprar.setOnAction(e -> abrirDialogoCompra());
        vender.setOnAction(e -> abrirDialogoVenda());
        editar.setOnAction(e -> abrirDialogoEdicao());
        remover.setOnAction(e -> removerSelecionado());
        atualizarPrecos.setOnAction(e -> {
            try {
                carteira.atualizarInformacoes();
                atualizar();
            } catch (InvalidAssetException ex) {
                System.out.println("Erro ao atualizar informações: " + ex.getMessage());
            }
        });
        
        relGeral.setOnAction(e ->
                mostrarTexto("Relatório geral", reportService.gerarRelatorioGeral(carteira).gerar()));
        relTipo.setOnAction(e ->
                mostrarTexto("Relatório por tipo", reportService.gerarRelatorioPorTipo(carteira).gerar()));
        alertas.setOnAction(e -> mostrarTexto("Alertas", gerarRecomendacoes()));
        salvar.setOnAction(e -> salvar());
        sair.setOnAction(e -> app.mostrarLogin());

        FlowPane barra = new FlowPane(8, 8, cadastrar, comprar, vender, editar, remover,
                atualizarPrecos, relGeral, relTipo, alertas, salvar, sair);
        barra.setPadding(new Insets(10));

        BorderPane conteudo = new BorderPane();
        conteudo.setCenter(tabela);
        VBox rodape = new VBox(6, labelTotal, barra);
        rodape.setPadding(new Insets(6));
        conteudo.setBottom(rodape);

        Tab aba = new Tab("Ativos", conteudo);
        return aba;
    }

    private void montarTabela() {
        TableColumn<FinancialAsset, String> colNome = new TableColumn<>("Ativo");
        colNome.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNome()));

        TableColumn<FinancialAsset, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipoNome()));

        TableColumn<FinancialAsset, Number> colQtd = new TableColumn<>("Quantidade");
        colQtd.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantidade()));

        TableColumn<FinancialAsset, Number> colPreco = new TableColumn<>("Preço por cota");
        colPreco.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrecoAtual()));

        TableColumn<FinancialAsset, Number> colInvest = new TableColumn<>("Investido");
        colInvest.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getInvestido()));

        TableColumn<FinancialAsset, Number> colAtual = new TableColumn<>("Valor atual");
        colAtual.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getValorAtual()));

        TableColumn<FinancialAsset, Number> colVar = new TableColumn<>("Variação");
        colVar.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().calcularVariaçãoMonetaria()));

        formatarDuasCasas(colQtd);
        formatarDuasCasas(colPreco);
        formatarDuasCasas(colInvest);
        formatarDuasCasas(colAtual);
        formatarDuasCasas(colVar);

        tabela.getColumns().add(colNome);
        tabela.getColumns().add(colTipo);
        tabela.getColumns().add(colQtd);
        tabela.getColumns().add(colPreco);
        tabela.getColumns().add(colInvest);
        tabela.getColumns().add(colAtual);
        tabela.getColumns().add(colVar);
        tabela.setItems(dados);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /** Exibe os valores numéricos da coluna com duas casas decimais. */
    private void formatarDuasCasas(TableColumn<FinancialAsset, Number> coluna) {
        coluna.setCellFactory(tc -> new TableCell<FinancialAsset, Number>() {
            @Override
            protected void updateItem(Number valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : String.format("%.2f", valor.doubleValue()));
            }
        });
    }

    // ------------------------------------------------------------------
    // Aba de dashboard
    // ------------------------------------------------------------------

    private Tab criarAbaDashboard() {
        // ----- Ativos na Carteira: doughnut + total no centro + legenda -----
        graficoPizza.setTitle(null);
        graficoPizza.setLegendVisible(false);
        graficoPizza.setLabelsVisible(false);
        graficoPizza.setMinSize(260, 260);
        graficoPizza.setPrefSize(260, 260);
        graficoPizza.setMaxSize(260, 260);

        labelCentro.setStyle("-fx-text-fill: #e1e6e9; -fx-font-size: 16px; -fx-font-weight: bold;");
        Circle furo = new Circle(70, Color.web("#333333"));
        furo.setMouseTransparent(true);
        StackPane doughnut = new StackPane(graficoPizza, furo, labelCentro);

        legenda.setAlignment(Pos.CENTER_LEFT);
        HBox blocoPizza = new HBox(24, doughnut, legenda);
        blocoPizza.setAlignment(Pos.CENTER_LEFT);

        Label tituloPizza = new Label("Ativos na Carteira");
        tituloPizza.setStyle("-fx-text-fill: #e1e6e9; -fx-font-size: 15px; -fx-font-weight: bold;");
        VBox painelPizza = new VBox(8, tituloPizza, blocoPizza);

        // ----- Evolução do Patrimônio: barras empilhadas por mês -----
        Label tituloEvol = new Label("Evolução do Patrimônio");
        tituloEvol.setStyle("-fx-text-fill: #e1e6e9; -fx-font-size: 15px; -fx-font-weight: bold;");
        graficoBarras.setTitle(null);
        graficoBarras.setLegendVisible(false);
        graficoBarras.getXAxis().setLabel("Mês");
        graficoBarras.getYAxis().setLabel("R$");
        graficoBarras.setPrefHeight(280);
        VBox painelEvol = new VBox(8, tituloEvol, graficoBarras);

        VBox conteudo = new VBox(20, painelPizza, painelEvol);
        conteudo.setPadding(new Insets(12));

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #333333; -fx-background-color: #333333;");
        return new Tab("Dashboard", scroll);
    }

    // ------------------------------------------------------------------
    // Ações
    // ------------------------------------------------------------------

    private void abrirDialogoCadastro() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Cadastrar ativo");

        ChoiceBox<String> tipo = new ChoiceBox<>(FXCollections.observableArrayList(
                "Ação", "Fii", "Criptomoeda", "FundoDeInvestimento", "TituloRendaFixa"));
                tipo.getSelectionModel().selectFirst();
        
        
        TextField nome = new TextField();
        
        nome.setPromptText("Código (ex.: PETR4)");

        Label infoNome = new Label("ⓘ");
        infoNome.setStyle("-fx-font-size: 14px; -fx-text-fill: #9aa5ab; -fx-cursor: hand;");
        Tooltip dicaNome = new Tooltip("Use o nome registrado no URL do site CoinMarketCap");
        dicaNome.setShowDelay(Duration.millis(150));
        Tooltip.install(infoNome, dicaNome);
        HBox campoNome = new HBox(6, nome, infoNome);
        campoNome.setAlignment(Pos.CENTER_LEFT);

        TextField valor = new TextField();
        valor.setPromptText("Valor investido");

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(10);
        grade.setPadding(new Insets(10));
        grade.addRow(0, new Label("Tipo:"), tipo);
        grade.addRow(1, new Label("Nome:"), campoNome);
        grade.addRow(2, new Label("Valor:"), valor);
        
        dialogo.getDialogPane().setContent(grade);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int dinheiro = Integer.parseInt(valor.getText().trim());
                carteira.cadastra(tipo.getValue(), nome.getText().trim(), dinheiro);
                atualizar();
            } catch (NumberFormatException ex) {
                erro("Valor inválido: informe um número inteiro.");
            } catch (InvalidAssetException ex) {
                erro(ex.getMessage());
            }
        }
    }

    private void abrirDialogoCompra() {
        FinancialAsset selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            erro("Selecione um ativo para comprar.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Comprar " + selecionado.getNome());

        Label preco = new Label(String.format("R$ %.2f", selecionado.getPrecoAtual()));
        TextField valor = new TextField();
        valor.setPromptText("Ex.: 500.00");

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(10);
        grade.setPadding(new Insets(10));
        grade.addRow(0, new Label("Preço por cota:"), preco);
        grade.addRow(1, new Label("Valor (R$):"), valor);

        dialogo.getDialogPane().setContent(grade);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                float dinheiro = Float.parseFloat(valor.getText().trim().replace(",", "."));
                carteira.compra(selecionado.getNome(), dinheiro);
                atualizar();
            } catch (NumberFormatException ex) {
                erro("Valor inválido: informe um número.");
            } catch (InvalidAssetException ex) {
                erro(ex.getMessage());
            }
        }
    }

    private void abrirDialogoVenda() {
        FinancialAsset selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            erro("Selecione um ativo para vender.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Vender " + selecionado.getNome());

        Label possuida = new Label(String.format("%.2f", selecionado.getQuantidade()));
        Label preco = new Label(String.format("R$ %.2f", selecionado.getPrecoAtual()));
        TextField quantidade = new TextField();
        quantidade.setPromptText("Ex.: 10");

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(10);
        grade.setPadding(new Insets(10));
        grade.addRow(0, new Label("Quantidade possuída:"), possuida);
        grade.addRow(1, new Label("Preço por cota:"), preco);
        grade.addRow(2, new Label("Quantidade a vender:"), quantidade);

        dialogo.getDialogPane().setContent(grade);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                float qtd = Float.parseFloat(quantidade.getText().trim().replace(",", "."));
                float recebido = carteira.vender(selecionado.getNome(), qtd);
                atualizar();
                info(String.format("Venda realizada: R$ %.2f", recebido));
            } catch (NumberFormatException ex) {
                erro("Quantidade inválida: informe um número.");
            } catch (InvalidAssetException ex) {
                erro(ex.getMessage());
            }
        }
    }

    private void abrirDialogoEdicao() {
        FinancialAsset selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            erro("Selecione um ativo para editar.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar " + selecionado.getNome());

        TextField quantidade = new TextField(String.valueOf(selecionado.getQuantidade()));
        TextField investido = new TextField(String.valueOf(selecionado.getInvestido()));
        DatePicker data = new DatePicker(LocalDate.now());

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(10);
        grade.setPadding(new Insets(10));
        grade.addRow(0, new Label("Quantidade:"), quantidade);
        grade.addRow(1, new Label("Investido:"), investido);
        grade.addRow(2, new Label("Data:"), data);

        dialogo.getDialogPane().setContent(grade);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                float novaQtd = Float.parseFloat(quantidade.getText().trim());
                float novoInvest = Float.parseFloat(investido.getText().trim());
                carteira.editar(selecionado.getNome(), novaQtd, novoInvest);
                // registra o estado da carteira no mês da data escolhida (gráfico de evolução)
                LocalDate escolhida = data.getValue() != null ? data.getValue() : LocalDate.now();
                userController.getUsuarioAtual().registrarSnapshotMensal(
                        YearMonth.from(escolhida),
                        carteira.calcularTotalInvestido(),
                        carteira.calcularValorTotal());
                atualizar();
            } catch (NumberFormatException ex) {
                erro("Valores inválidos: informe números.");
            } catch (InvalidAssetException ex) {
                erro(ex.getMessage());
            }
        }
    }

    private void removerSelecionado() {
        FinancialAsset selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            erro("Selecione um ativo para remover.");
            return;
        }
        carteira.remove(selecionado.getNome());
        atualizar();
    }

    private void salvar() {
        try {
            userController.salvarUsuarioAtual();
            info("Carteira salva com sucesso.");
        } catch (PersistenceException ex) {
            erro(ex.getMessage());
        }
    }

    private String gerarRecomendacoes() {
        if (carteira.getInvestimentos().isEmpty()) {
            return "Nenhum ativo cadastrado.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(Alertas demonstrativos)\n\n");
        for (FinancialAsset ativo : carteira.getInvestimentos()) {
            double variacao = ativo.calcularVariaçãoMonetaria();
            String situacao;
            if (variacao > 0) {
                situacao = "subindo";
            } else if (variacao < 0) {
                situacao = "caindo";
            } else {
                situacao = "estável";
            }
            sb.append(String.format("%s (%s): %s%n", ativo.getNome(), ativo.getTipoNome(), situacao));
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Atualização de estado / gráficos
    // ------------------------------------------------------------------

    private void atualizar() {
        dados.setAll(carteira.getInvestimentos());
        tabela.refresh();
        labelTotal.setText(String.format(
                "Total investido: R$ %.2f  |  Valor atual: R$ %.2f  |  Rentabilidade: %.2f%%",
                carteira.calcularTotalInvestido(),
                carteira.calcularValorTotal(),
                carteira.calcularRentabilidadeGeral()));
        atualizarGraficos();
    }

    private void atualizarGraficos() {
        // ---- Doughnut "Ativos na Carteira" + legenda ----
        Map<String, List<FinancialAsset>> grupos = carteira.organizarPorTipo();
        double totalAtual = carteira.calcularValorTotal();

        ObservableList<PieChart.Data> fatias = FXCollections.observableArrayList();
        legenda.getChildren().clear();

        int i = 0;
        for (Map.Entry<String, List<FinancialAsset>> grupo : grupos.entrySet()) {
            double atual = 0;
            for (FinancialAsset ativo : grupo.getValue()) {
                atual += ativo.getValorAtual();
            }
            fatias.add(new PieChart.Data(grupo.getKey(), atual));
            double pct = totalAtual > 0 ? (atual / totalAtual) * 100 : 0;
            legenda.getChildren().add(linhaLegenda(PALETA[i % PALETA.length], grupo.getKey(), pct, atual));
            i++;
        }
        graficoPizza.setData(fatias);
        labelCentro.setText(String.format(Locale.forLanguageTag("pt-BR"), "R$ %.0f", totalAtual));

        // ---- "Evolução do Patrimônio" por mês (barras empilhadas) ----
        Map<String, double[]> historico =
                new TreeMap<>(userController.getUsuarioAtual().getHistoricoMensal());
        // inclui/atualiza o mês atual com os valores ao vivo
        historico.put(YearMonth.now().toString(),
                new double[]{carteira.calcularTotalInvestido(), totalAtual});

        XYChart.Series<String, Number> seriePatrimonio = new XYChart.Series<>();
        seriePatrimonio.setName("Patrimônio");

        for (Map.Entry<String, double[]> mes : historico.entrySet()) {
            double valorAtual = mes.getValue()[1];
            seriePatrimonio.getData().add(new XYChart.Data<>(formatarMes(mes.getKey()), valorAtual));
        }
        graficoBarras.getData().setAll(seriePatrimonio);

        // Cores (após o layout criar os nós).
        Platform.runLater(() -> {
            int k = 0;
            for (PieChart.Data fatia : graficoPizza.getData()) {
                if (fatia.getNode() != null) {
                    fatia.getNode().setStyle("-fx-pie-color: " + PALETA[k % PALETA.length] + ";");
                }
                k++;
            }
            estilizarBarras(seriePatrimonio, "#e0a33e");
        });
    }

    private void estilizarBarras(XYChart.Series<String, Number> serie, String cor) {
        for (XYChart.Data<String, Number> dado : serie.getData()) {
            if (dado.getNode() != null) {
                dado.getNode().setStyle("-fx-bar-fill: " + cor + ";");
            }
        }
    }

    private HBox linhaLegenda(String cor, String tipo, double pct, double valor) {
        Circle ponto = new Circle(6, Color.web(cor));
        Label nome = new Label(tipo);
        nome.setStyle("-fx-text-fill: #e1e6e9;");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        Label info = new Label(String.format(Locale.forLanguageTag("pt-BR"), "%.1f%%   R$ %.2f", pct, valor));
        info.setStyle("-fx-text-fill: #b9c6cf;");
        HBox linha = new HBox(8, ponto, nome, espaco, info);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setMinWidth(260);
        return linha;
    }

    /** Converte a chave "aaaa-mm" em "mm/aa". */
    private String formatarMes(String chave) {
        String[] partes = chave.split("-");
        if (partes.length == 2) {
            return partes[1] + "/" + partes[0].substring(2);
        }
        return chave;
    }

    // ------------------------------------------------------------------
    // Utilidades de UI
    // ------------------------------------------------------------------

    private void mostrarTexto(String titulo, String texto) {
        TextArea area = new TextArea(texto);
        area.setEditable(false);
        area.setWrapText(true);

        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.getDialogPane().setContent(area);
        alerta.getDialogPane().setPrefSize(480, 360);
        alerta.showAndWait();
    }

    private void info(String mensagem) {
        Alert alerta = new Alert(AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void erro(String mensagem) {
        Alert alerta = new Alert(AlertType.ERROR, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
