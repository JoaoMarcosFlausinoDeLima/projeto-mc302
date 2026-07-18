package projeto.gui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import projeto.servicos.UserController;

/**
 * Ponto de entrada da interface gráfica do Invest&Multiply.
 *
 * <p>Gerencia a troca de telas (login e carteira) usando um único
 * {@link Stage} e mantém o {@link UserController} compartilhado.</p>
 */
public class MainApp extends Application {

    private Stage stage;
    private final UserController userController = new UserController();

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Invest&Multiply");

        Image icone = Recursos.carregarIcone();
        if (icone != null) {
            stage.getIcons().add(icone);
        }

        mostrarLogin();
        stage.show();
    }

    /** Exibe a tela de login/cadastro. */
    public void mostrarLogin() {
        stage.setScene(new TelaLogin(this, userController).criarCena());
    }

    /** Exibe a tela principal da carteira do usuário logado. */
    public void mostrarCarteira() {
        stage.setScene(new TelaCarteira(this, userController).criarCena());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
