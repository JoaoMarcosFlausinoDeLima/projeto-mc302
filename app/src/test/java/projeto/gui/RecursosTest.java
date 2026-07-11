package projeto.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.image.Image;

class RecursosTest {

    @Test
    void carregarLogoShouldReturnImage() {
        Image imagem = Recursos.carregarLogo();
        assertNotNull(imagem);
    }

    @Test
    void carregarIconeShouldReturnImage() {
        Image icone = Recursos.carregarIcone();
        assertNotNull(icone);
    }

   
    @Test
    void carregarShouldReturnNullWhenResourceIsMissing() {
        Image imagem = new RecursosTest().carregarViaReflection("/imagens/arquivo-inexistente.png");
        assertNull(imagem);
    }

    private Image carregarViaReflection(String caminho) {
        try {
            java.lang.reflect.Method metodo = Recursos.class.getDeclaredMethod("carregar", String.class);
            metodo.setAccessible(true);
            return (Image) metodo.invoke(null, caminho);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
