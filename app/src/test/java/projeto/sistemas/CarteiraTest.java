package projeto.sistemas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import projeto.excecoes.InvalidAssetException;

import projeto.investimentos.FinancialAsset;

class CarteiraTest {

    private static final Path CARTEIRA_FILE = Path.of("carteira.json");
    private static final Path CARTEIRA_BACKUP = Path.of("carteira.json.bak");
    private boolean carteiraBackedUp;

    @BeforeEach
    void backupExistingCarteira() throws IOException {
        if (Files.exists(CARTEIRA_FILE)) {
            Files.copy(CARTEIRA_FILE, CARTEIRA_BACKUP, StandardCopyOption.REPLACE_EXISTING);
            carteiraBackedUp = true;
        }
    }

    @AfterEach
    void restoreCarteira() throws IOException {
        if (carteiraBackedUp) {
            Files.move(CARTEIRA_BACKUP, CARTEIRA_FILE, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(CARTEIRA_FILE);
            Files.deleteIfExists(CARTEIRA_BACKUP);
        }
    }

    @Test
    void shouldAddAssetsAndCalculateTotals() throws InvalidAssetException {
        Carteira carteira = new Carteira();
        carteira.cadastra("Ação", "PETR4", 100);
        carteira.cadastra("Fii", "HGLG11", 50);

        assertEquals(150.0, carteira.calcularValorTotal(), 0.0001);
        assertEquals(0, carteira.calcularVariacaoTotal(), 0.0001);
        for (FinancialAsset ativo : carteira.getInvestimentos()) {
            ativo.setPrecoAtual(10.0);
            ativo.comprar(100);
        }

        assertEquals(2, carteira.getInvestimentos().size());
        assertEquals(350.0, carteira.calcularTotalInvestido(), 0.0001);
        assertNotEquals(200.0, carteira.calcularValorTotal(), 0.0001);
        assertNotEquals(0, carteira.calcularVariacaoTotal(), 0.0001);
        
    }

    @Test
    void cadastraWithInvalidTypeThrows() {
        Carteira carteira = new Carteira();
        assertThrows(InvalidAssetException.class, () -> carteira.cadastra("Bitcoin", "btc", 100));
    }

    @Test
    void compraUnknownAssetThrows() {
        Carteira carteira = new Carteira();
        assertThrows(InvalidAssetException.class, () -> carteira.compra("NAOEXISTE", 100));
    }

    @Test
    void shouldOrganizeInvestmentsByTipo() throws InvalidAssetException {
        Carteira carteira = new Carteira();
        carteira.cadastra("Ação", "PETR4", 100);
        carteira.cadastra("Fii", "HGLG11", 50);

        var grupos = carteira.organizarPorTipo();

        assertEquals(2, grupos.size());
        assertEquals(1, grupos.get("Ação").size());
        assertEquals(1, grupos.get("FII").size());
    }

    @Test
    void salvarAndCarregarShouldPreserveInvestments() throws Exception {
        Carteira carteira = new Carteira();
        carteira.cadastra("Fii", "HGLG11", 100);
        carteira.getInvestimentos().get(0).setPrecoAtual(10.0);
        carteira.getInvestimentos().get(0).comprar(100);

        carteira.salvar();

        Carteira novaCarteira = new Carteira();
        novaCarteira.carregar();

        assertEquals(1, novaCarteira.getInvestimentos().size());
        assertEquals("HGLG11", novaCarteira.getInvestimentos().get(0).getNome());
    }
}
