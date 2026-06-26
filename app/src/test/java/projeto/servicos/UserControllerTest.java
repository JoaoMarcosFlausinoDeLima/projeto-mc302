package projeto.servicos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import projeto.excecoes.PersistenceException;
import projeto.usuario.Usuario;

class UserControllerTest {

    @Test
    void cadastrarUsuarioCreatesUser() throws PersistenceException {
        UserController controller = new UserController();
        Usuario usuario = controller.cadastrarUsuario("testuser", "senha123");

        assertTrue(usuario.autenticar("senha123"));
        assertTrue(controller.realizarLogin("testuser", "senha123"));
    }

    @Test
    void salvarUsuarioAtualWithoutLoginThrows() {
        UserController controller = new UserController();

        assertThrows(PersistenceException.class, controller::salvarUsuarioAtual);
    }

    @Test
    void loginWithWrongPasswordReturnsFalse() throws PersistenceException {
        UserController controller = new UserController();
        controller.cadastrarUsuario("user2", "senha123");

        assertFalse(controller.realizarLogin("user2", "senhaErrada"));
    }
}
