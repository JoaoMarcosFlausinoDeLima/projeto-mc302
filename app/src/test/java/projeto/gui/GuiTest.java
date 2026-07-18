package projeto.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

import projeto.investimentos.FinancialAsset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de interface: abrem o app JavaFX real e o dirigem programaticamente
 * (preenchem campos, clicam botões, confirmam diálogos).
 *
 * <p>Requerem ambiente gráfico (abrem janelas) e internet (cotações via
 * Fundamentus/CoinMarketCap). O diretório de trabalho do Gradle para testes é
 * isolado em {@code build/test-work}, então os dados criados aqui não se
 * misturam aos dados reais do app.</p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GuiTest {

    private static final String USUARIO = "teste_gui";
    private static final String SENHA = "123";
    private static final String PREF_USUARIO = "usuario_lembrado";

    private static final Preferences prefs = Preferences.userNodeForPackage(TelaLogin.class);
    private static String prefAnterior;
    private static final List<Window> dialogosTratados = new ArrayList<>();

    @BeforeAll
    static void subirToolkit() throws Exception {
        // preserva o "Lembrar-me" real do usuário para restaurar no fim
        prefAnterior = prefs.get(PREF_USUARIO, null);
        new File("data/usuario_" + USUARIO + ".json").delete();

        Platform.setImplicitExit(false);
        CountDownLatch pronto = new CountDownLatch(1);
        try {
            Platform.startup(pronto::countDown);
        } catch (IllegalStateException jaIniciado) {
            pronto.countDown();
        }
        assertTrue(pronto.await(10, TimeUnit.SECONDS), "toolkit JavaFX não subiu");
        abrirApp();
    }

    @AfterAll
    static void restaurarEstado() throws Exception {
        if (prefAnterior == null) {
            prefs.remove(PREF_USUARIO);
        } else {
            prefs.put(PREF_USUARIO, prefAnterior);
        }
        fx(() -> { for (Window w : List.copyOf(Window.getWindows())) w.hide(); });
        new File("data/usuario_" + USUARIO + ".json").delete();
    }

    // ------------------------------------------------------------------
    // Testes (em ordem: cada um continua do estado do anterior)
    // ------------------------------------------------------------------

    @Test
    @Order(1)
    @Timeout(180)
    void fluxoCompletoDaCarteira() throws Exception {
        // --- cria conta com Lembrar-me marcado ---
        Scene login = cenaPrincipal();
        fx(() -> {
            preencherLogin(login, USUARIO, SENHA);
            ((CheckBox) login.getRoot().lookup(".check-box")).setSelected(true);
            botao(login.getRoot(), "CRIAR CONTA").fire();
        });
        Thread.sleep(300);
        Scene carteira = cenaPrincipal();
        assertNotNull(carteira.getRoot().lookup(".tab-pane"), "tela da carteira não abriu");

        // --- cadastra Ação PETR4 ---
        quandoDialogo(dp -> dp.lookup(".choice-box") != null, dp -> {
            escolherTipo(dp, "Ação");
            campoPorPrompt(dp, "Código (ex.: PETR4)").setText("PETR4");
            campoPorPrompt(dp, "Valor investido").setText("1000");
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Cadastrar").fire());

        FinancialAsset petr = ativo(carteira, 0);
        assertEquals("PETR4", petr.getNome());
        assertTrue(petr.getPrecoAtual() > 0, "preço por cota da ação não veio do backend");
        assertTrue(Float.isFinite(petr.getQuantidade()), "quantidade inválida");

        // --- cadastra Criptomoeda bitcoin (parse de preço com vírgula de milhar) ---
        quandoDialogo(dp -> dp.lookup(".choice-box") != null, dp -> {
            escolherTipo(dp, "Criptomoeda");
            campoPorPrompt(dp, "Código (ex.: PETR4)").setText("bitcoin");
            campoPorPrompt(dp, "Valor investido").setText("1000");
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Cadastrar").fire());

        FinancialAsset btc = ativo(carteira, 1);
        assertTrue(btc.getPrecoAtual() > 0, "preço da cripto não veio do backend");
        assertTrue(Float.isFinite(btc.getQuantidade()), "quantidade da cripto inválida (preço zerado na compra?)");

        // --- compra +500 de PETR4 ---
        fx(() -> tabela(carteira).getSelectionModel().select(0));
        quandoDialogo(dp -> campoPorPrompt(dp, "Ex.: 500.00") != null, dp -> {
            campoPorPrompt(dp, "Ex.: 500.00").setText("500");
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Comprar").fire());
        assertEquals(1500.0, petr.getInvestido(), 0.01, "compra não somou ao investido");

        // --- vende 5 cotas de PETR4 ---
        float qtdAntes = petr.getQuantidade();
        AtomicReference<String> alertaVenda = new AtomicReference<>("");
        quandoDialogo(dp -> campoPorPrompt(dp, "Ex.: 10") != null, dp -> {
            campoPorPrompt(dp, "Ex.: 10").setText("5");
            ok(dp);
        });
        quandoDialogo(dp -> texto(dp).contains("Venda realizada"), dp -> {
            alertaVenda.set(texto(dp));
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Vender").fire());
        assertTrue(alertaVenda.get().startsWith("Venda realizada"), "alerta de venda não apareceu");
        assertEquals(qtdAntes - 5, petr.getQuantidade(), 0.01, "quantidade não caiu após a venda");
        assertTrue(petr.getInvestido() < 1500, "investido não foi reduzido proporcionalmente na venda");

        // --- probe: vender mais do que possui ---
        AtomicReference<String> erroVenda = new AtomicReference<>("");
        quandoDialogo(dp -> campoPorPrompt(dp, "Ex.: 10") != null, dp -> {
            campoPorPrompt(dp, "Ex.: 10").setText("999999");
            ok(dp);
        });
        quandoDialogo(dp -> texto(dp).contains("insuficiente"), dp -> {
            erroVenda.set(texto(dp));
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Vender").fire());
        assertTrue(erroVenda.get().contains("insuficiente"), "venda excessiva deveria dar erro");

        // --- probe: compra com valor não numérico ---
        AtomicReference<String> erroCompra = new AtomicReference<>("");
        quandoDialogo(dp -> campoPorPrompt(dp, "Ex.: 500.00") != null, dp -> {
            campoPorPrompt(dp, "Ex.: 500.00").setText("abc");
            ok(dp);
        });
        quandoDialogo(dp -> texto(dp).contains("inválido"), dp -> {
            erroCompra.set(texto(dp));
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Comprar").fire());
        assertTrue(erroCompra.get().contains("inválido"), "valor não numérico deveria dar erro");

        // --- janela Alertas ---
        AtomicReference<String> textoAlertas = new AtomicReference<>("");
        quandoDialogo(dp -> dp.lookup(".text-area") != null, dp -> {
            textoAlertas.set(((TextArea) dp.lookup(".text-area")).getText());
            ok(dp);
        });
        fx(() -> botao(carteira.getRoot(), "Alertas").fire());
        assertTrue(textoAlertas.get().contains("PETR4"), "janela Alertas não listou os ativos");

        // --- salva em disco ---
        quandoDialogo(dp -> texto(dp).contains("salva"), dp -> ok(dp));
        fx(() -> botao(carteira.getRoot(), "Salvar").fire());
        Thread.sleep(200);
        assertTrue(new File("data/usuario_" + USUARIO + ".json").exists(), "arquivo do usuário não foi salvo");
    }

    @Test
    @Order(2)
    @Timeout(60)
    void lembrarMePreencheUsuarioEmNovaSessao() throws Exception {
        abrirApp(); // simula fechar e abrir o app de novo
        Scene login = cenaPrincipal();
        fx(() -> {
            assertEquals(USUARIO, campoUsuario(login).getText(), "usuário não veio preenchido");
            assertTrue(((CheckBox) login.getRoot().lookup(".check-box")).isSelected(),
                    "checkbox Lembrar-me deveria vir marcado");
        });
    }

    @Test
    @Order(3)
    @Timeout(60)
    void carteiraSalvaRecarregaEmNovaSessao() throws Exception {
        Scene login = cenaPrincipal();
        fx(() -> {
            preencherLogin(login, USUARIO, SENHA);
            botao(login.getRoot(), "ENTRAR").fire();
        });
        Thread.sleep(300);
        Scene carteira = cenaPrincipal();
        assertNotNull(carteira.getRoot().lookup(".tab-pane"), "login não entrou na carteira");

        assertEquals(2, tabela(carteira).getItems().size(), "carteira não recarregou os 2 ativos");
        assertEquals("PETR4", ativo(carteira, 0).getNome());
        assertEquals("bitcoin", ativo(carteira, 1).getNome());
        assertTrue(ativo(carteira, 1).getPrecoAtual() > 0, "preço da cripto se perdeu ao recarregar");
    }

    // ------------------------------------------------------------------
    // Utilitários de direção da GUI
    // ------------------------------------------------------------------

    /** Fecha as janelas abertas e sobe o app do zero (nova "sessão"). */
    private static void abrirApp() throws Exception {
        fx(() -> {
            for (Window w : List.copyOf(Window.getWindows())) {
                w.hide();
            }
            new MainApp().start(new Stage());
        });
        Thread.sleep(300);
    }

    /** Executa no thread do JavaFX e espera terminar, propagando falhas. */
    private static void fx(Runnable acao) throws Exception {
        CountDownLatch fim = new CountDownLatch(1);
        AtomicReference<Throwable> falha = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                acao.run();
            } catch (Throwable t) {
                falha.set(t);
            } finally {
                fim.countDown();
            }
        });
        assertTrue(fim.await(120, TimeUnit.SECONDS), "ação na GUI não terminou (diálogo esperado não apareceu?)");
        if (falha.get() != null) {
            if (falha.get() instanceof AssertionError ae) {
                throw ae;
            }
            throw new RuntimeException(falha.get());
        }
    }

    /**
     * Agenda uma ação para quando abrir um diálogo que satisfaça o predicado.
     * Deve ser chamado ANTES de disparar o botão que abre o diálogo, porque
     * showAndWait bloqueia num event loop aninhado.
     */
    private static void quandoDialogo(Predicate<DialogPane> cond, Consumer<DialogPane> acao) {
        Platform.runLater(new Runnable() {
            int tentativas = 0;

            @Override
            public void run() {
                for (Window w : Window.getWindows()) {
                    if (w.isShowing() && w.getScene() != null
                            && w.getScene().getRoot() instanceof DialogPane dp
                            && !dialogosTratados.contains(w) && cond.test(dp)) {
                        dialogosTratados.add(w);
                        acao.accept(dp);
                        return;
                    }
                }
                if (++tentativas < 500000) {
                    Platform.runLater(this);
                }
            }
        });
    }

    private static Scene cenaPrincipal() throws Exception {
        AtomicReference<Scene> cena = new AtomicReference<>();
        fx(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage st && st.isShowing()
                        && !(w.getScene().getRoot() instanceof DialogPane)) {
                    cena.set(w.getScene());
                }
            }
        });
        assertNotNull(cena.get(), "nenhuma janela principal aberta");
        return cena.get();
    }

    private static void preencherLogin(Scene login, String usuario, String senha) {
        campoUsuario(login).setText(usuario);
        for (Node n : login.getRoot().lookupAll(".text-input")) {
            if (n instanceof PasswordField p) {
                p.setText(senha);
            }
        }
    }

    private static TextField campoUsuario(Scene login) {
        for (Node n : login.getRoot().lookupAll(".text-input")) {
            if (!(n instanceof PasswordField) && n instanceof TextField t) {
                return t;
            }
        }
        throw new AssertionError("campo de usuário não encontrado");
    }

    @SuppressWarnings("unchecked")
    private static void escolherTipo(DialogPane dp, String tipo) {
        ((ChoiceBox<String>) dp.lookup(".choice-box")).setValue(tipo);
    }

    private static void ok(DialogPane dp) {
        for (ButtonType bt : dp.getButtonTypes()) {
            if (bt == ButtonType.OK || bt.getButtonData().isDefaultButton()) {
                ((Button) dp.lookupButton(bt)).fire();
                return;
            }
        }
        ((Button) dp.lookupButton(dp.getButtonTypes().get(0))).fire();
    }

    private static String texto(DialogPane dp) {
        return dp.getContentText() == null ? "" : dp.getContentText();
    }

    private static Button botao(Node raiz, String rotulo) {
        for (Node n : raiz.lookupAll(".button")) {
            if (n instanceof Button b && rotulo.equals(b.getText())) {
                return b;
            }
        }
        throw new AssertionError("botão não encontrado: " + rotulo);
    }

    private static TextField campoPorPrompt(Node raiz, String prompt) {
        for (Node n : raiz.lookupAll(".text-field")) {
            if (n instanceof TextField t && prompt.equals(t.getPromptText())) {
                return t;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static TableView<FinancialAsset> tabela(Scene cena) {
        return (TableView<FinancialAsset>) cena.getRoot().lookup(".table-view");
    }

    private static FinancialAsset ativo(Scene cena, int linha) throws Exception {
        AtomicReference<FinancialAsset> ref = new AtomicReference<>();
        fx(() -> ref.set(tabela(cena).getItems().get(linha)));
        return ref.get();
    }
}
