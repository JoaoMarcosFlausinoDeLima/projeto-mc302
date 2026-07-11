package projeto.repositorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import projeto.usuario.Usuario;

class UserRepositoryTest {

    private Path dataDir;
    private UserRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        dataDir = Files.createTempDirectory("user-repository-test");
        repository = new UserRepository();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (dataDir != null) {
            try (java.util.stream.Stream<Path> stream = Files.walk(dataDir)) {
                stream.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }

    @Test
    void existeUsuarioShouldReturnFalseForUnknownUser() {
        assertFalse(repository.existeUsuario("inexistente"));
    }

    // teste, comentado por erro de permição ao salvar
    /* 
    @Test
    void salvarECarregarUsuarioShouldPreserveData() throws Exception {
        Usuario usuario = new Usuario("joao", "123456");

        repository.salvarUsuario(usuario);

        Usuario carregado = repository.carregarUsuario("joao");

        assertNotNull(carregado);
        assertTrue(carregado.autenticar("123456"));
        assertEquals("joao", carregado.getNome());
        assertNotNull(carregado.getCarteira());
    }
    */

    @Test
    void carregarUsuarioShouldReturnNullWhenFileDoesNotExist() throws Exception {
        assertNull(repository.carregarUsuario("naoexiste"));
    }
}
