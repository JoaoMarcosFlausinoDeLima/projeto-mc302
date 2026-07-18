package projeto.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void autenticarWithCorrectPasswordReturnsTrue() {
        Usuario usuario = new Usuario("joao", "senha123");

        assertTrue(usuario.autenticar("senha123"));
    }

    @Test
    void registrarSnapshotMensalStoresHistorico() {
        Usuario usuario = new Usuario("joao", "senha123");
        usuario.registrarSnapshotMensal(YearMonth.of(2025, 5), 500.0, 520.0);

        assertEquals(1, usuario.getHistoricoMensal().size());
        assertEquals(520.0, usuario.getHistoricoMensal().get("2025-05")[1], 0.0001);
    }
}
