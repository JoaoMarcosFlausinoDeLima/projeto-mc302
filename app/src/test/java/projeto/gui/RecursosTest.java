package projeto.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

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


}
