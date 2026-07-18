package projeto.gui;

import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Acesso centralizado a recursos visuais da interface (imagens, ícones).
 */
public final class Recursos {

    /** Caminho do logo no classpath (src/main/resources/imagens/logo.png). */
    public static final String CAMINHO_LOGO = "/imagens/logo.png";

    /** Caminho do ícone (só o escudo) no classpath (src/main/resources/imagens/icone.png). */
    public static final String CAMINHO_ICONE = "/imagens/icone.png";

    /** Caminho do tema escuro no classpath (src/main/resources/css/dark.css). */
    public static final String CAMINHO_TEMA_DARK = "/css/dark.css";

    private Recursos() {
    }

    /**
     * Aplica o tema escuro a uma cena, se a folha de estilo existir.
     *
     * @param cena cena a ser estilizada
     */
    public static void aplicarTemaDark(javafx.scene.Scene cena) {
        java.net.URL css = Recursos.class.getResource(CAMINHO_TEMA_DARK);
        if (css != null) {
            cena.getStylesheets().add(css.toExternalForm());
        }
    }

    /**
     * Carrega o logo do aplicativo.
     *
     * @return a imagem do logo, ou {@code null} se o arquivo não for encontrado
     */
    public static Image carregarLogo() {
        return carregar(CAMINHO_LOGO);
    }

    /**
     * Carrega o ícone do aplicativo (escudo, sem o texto). Se não existir,
     * usa o logo completo como alternativa.
     *
     * @return a imagem do ícone, ou {@code null} se nenhuma imagem for encontrada
     */
    public static Image carregarIcone() {
        Image icone = carregar(CAMINHO_ICONE);
        return icone != null ? icone : carregarLogo();
    }

    private static Image carregar(String caminho) {
        InputStream is = Recursos.class.getResourceAsStream(caminho);
        if (is == null) {
            return null;
        }
        return new Image(is);
    }
}
