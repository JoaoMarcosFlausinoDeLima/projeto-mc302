package projeto.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import projeto.excecoes.PersistenceException;
import projeto.servicos.UserController;

/**
 * Tela de login e cadastro de usuário, com campos arredondados, ícones e
 * botão de ação em destaque.
 */
public class TelaLogin {

    private static final String COR_FUNDO = "#333333";
    private static final String COR_BORDA = "#5b8499";
    private static final String COR_TEXTO = "#cfe0ea";
    private static final double LARGURA = 300;

    private static final String ESTILO_BTN_VERDE =
            "-fx-background-color: #2ecc71; -fx-text-fill: #10261a; -fx-font-weight: bold;"
            + " -fx-font-size: 14px; -fx-background-radius: 24; -fx-cursor: hand;";
    private static final String ESTILO_BTN_VERDE_HOVER =
            "-fx-background-color: #37d97e; -fx-text-fill: #10261a; -fx-font-weight: bold;"
            + " -fx-font-size: 14px; -fx-background-radius: 24; -fx-cursor: hand;";
    private static final String ESTILO_BTN_GHOST =
            "-fx-background-color: transparent; -fx-text-fill: " + COR_TEXTO + ";"
            + " -fx-border-color: " + COR_BORDA + "; -fx-border-radius: 24; -fx-background-radius: 24;"
            + " -fx-cursor: hand;";

    private final MainApp app;
    private final UserController userController;

    public TelaLogin(MainApp app, UserController userController) {
        this.app = app;
        this.userController = userController;
    }

    public Scene criarCena() {
        VBox raiz = new VBox(16);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(30));
        raiz.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        Image logo = Recursos.carregarLogo();
        if (logo != null) {
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(260);
            logoView.setPreserveRatio(true);
            raiz.getChildren().add(logoView);
        }

        TextField campoNome = new TextField();
        campoNome.setPromptText("Usuário");
        estilizarCampoTransparente(campoNome);
        HBox caixaNome = criarCampo(iconeUsuario(), campoNome);

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        estilizarCampoTransparente(campoSenha);
        HBox caixaSenha = criarCampo(iconeCadeado(), campoSenha);

        CheckBox lembrar = new CheckBox("Lembrar-me");
        lembrar.setStyle("-fx-text-fill: " + COR_TEXTO + ";");
        Hyperlink esqueceu = new Hyperlink("Esqueceu a senha?");
        esqueceu.setStyle("-fx-text-fill: #9fb8c6; -fx-font-style: italic; -fx-border-color: transparent;");
        esqueceu.setOnAction(e -> info("Recuperação de senha ainda não disponível."));
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox linhaOpcoes = new HBox(lembrar, espaco, esqueceu);
        linhaOpcoes.setMaxWidth(LARGURA);
        linhaOpcoes.setAlignment(Pos.CENTER_LEFT);

        Label mensagem = new Label();
        mensagem.setStyle("-fx-text-fill: #ff6b6b;");

        Button botaoEntrar = new Button("ENTRAR");
        botaoEntrar.setMaxWidth(Double.MAX_VALUE);
        botaoEntrar.setPrefHeight(42);
        botaoEntrar.setStyle(ESTILO_BTN_VERDE);
        botaoEntrar.setOnMouseEntered(e -> botaoEntrar.setStyle(ESTILO_BTN_VERDE_HOVER));
        botaoEntrar.setOnMouseExited(e -> botaoEntrar.setStyle(ESTILO_BTN_VERDE));

        Button botaoCadastrar = new Button("CRIAR CONTA");
        botaoCadastrar.setMaxWidth(Double.MAX_VALUE);
        botaoCadastrar.setPrefHeight(38);
        botaoCadastrar.setStyle(ESTILO_BTN_GHOST);

        botaoEntrar.setOnAction(e -> {
            if (camposVazios(campoNome, campoSenha, mensagem)) {
                return;
            }
            try {
                if (userController.realizarLogin(campoNome.getText(), campoSenha.getText())) {
                    app.mostrarCarteira();
                } else {
                    mensagem.setText("Usuário ou senha inválidos.");
                }
            } catch (PersistenceException ex) {
                mensagem.setText(ex.getMessage());
            }
        });

        botaoCadastrar.setOnAction(e -> {
            if (camposVazios(campoNome, campoSenha, mensagem)) {
                return;
            }
            try {
                userController.cadastrarUsuario(campoNome.getText(), campoSenha.getText());
                app.mostrarCarteira();
            } catch (PersistenceException ex) {
                mensagem.setText(ex.getMessage());
            }
        });

        campoSenha.setOnAction(e -> botaoEntrar.fire());

        VBox formulario = new VBox(14, caixaNome, caixaSenha, linhaOpcoes,
                botaoEntrar, botaoCadastrar, mensagem);
        formulario.setAlignment(Pos.CENTER);
        formulario.setMaxWidth(LARGURA);

        raiz.getChildren().add(formulario);

        return new Scene(raiz, 440, 640);
    }

    // ------------------------------------------------------------------
    // Componentes estilizados
    // ------------------------------------------------------------------

    private HBox criarCampo(Node icone, Control campo) {
        HBox.setHgrow(campo, Priority.ALWAYS);
        HBox caixa = new HBox(10, icone, campo);
        caixa.setAlignment(Pos.CENTER_LEFT);
        caixa.setMaxWidth(LARGURA);
        caixa.setPadding(new Insets(8, 16, 8, 16));
        caixa.setStyle("-fx-background-color: #2c2c2c;"
                + " -fx-background-radius: 24; -fx-border-radius: 24;"
                + " -fx-border-color: " + COR_BORDA + "; -fx-border-width: 1.5;");
        return caixa;
    }

    private void estilizarCampoTransparente(TextField campo) {
        campo.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COR_TEXTO + ";"
                + " -fx-prompt-text-fill: #8aa6b5; -fx-font-size: 14px;");
    }

    /** Ícone simples de usuário desenhado com formas. */
    private Node iconeUsuario() {
        Circle cabeca = new Circle(9, 5, 4, Color.web(COR_BORDA));
        Arc corpo = new Arc(9, 18, 7, 7, 0, 180);
        corpo.setType(ArcType.ROUND);
        corpo.setFill(Color.web(COR_BORDA));
        Pane p = new Pane(cabeca, corpo);
        p.setMinSize(18, 20);
        p.setPrefSize(18, 20);
        p.setMaxSize(18, 20);
        return p;
    }

    /** Ícone simples de cadeado desenhado com formas. */
    private Node iconeCadeado() {
        Arc arco = new Arc(9, 9, 5, 5, 0, 180);
        arco.setType(ArcType.OPEN);
        arco.setStroke(Color.web(COR_BORDA));
        arco.setStrokeWidth(2);
        arco.setFill(Color.TRANSPARENT);
        Rectangle corpo = new Rectangle(3, 9, 12, 9);
        corpo.setArcWidth(4);
        corpo.setArcHeight(4);
        corpo.setFill(Color.web(COR_BORDA));
        Pane p = new Pane(arco, corpo);
        p.setMinSize(18, 20);
        p.setPrefSize(18, 20);
        p.setMaxSize(18, 20);
        return p;
    }

    private boolean camposVazios(TextField nome, PasswordField senha, Label mensagem) {
        if (nome.getText().isBlank() || senha.getText().isBlank()) {
            mensagem.setText("Preencha usuário e senha.");
            return true;
        }
        return false;
    }

    private void info(String mensagem) {
        Alert alerta = new Alert(AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
