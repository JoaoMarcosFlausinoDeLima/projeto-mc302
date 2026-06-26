package projeto.investimentos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FinancialAssetTest {

    private static final class TestAsset extends FinancialAsset {

        TestAsset(String nome, float dinheiro) {
            super(nome, dinheiro);
        }

        @Override
        public void atualizarInformacoes() {
            this.preco_atual = 10.0;
        }

        @Override
        public double render() {
            return 0;
        }

        @Override
        public void resumo() {
            // no-op for tests
        }

        @Override
        public String getTipoNome() {
            return "Teste";
        }
    }

    @Test
    void comprarIncreasesQuantityAndInvested() {
        TestAsset asset = new TestAsset("TEST", 100f);

        assertEquals(10f, asset.getQuantidade(), 0.0001);
        assertEquals(100f, asset.getInvestido(), 0.0001);
        assertEquals(100f, asset.getValorAtual(), 0.0001);
    }

    @Test
    void venderReducesQuantityAndReturnsValue() {
        TestAsset asset = new TestAsset("TEST", 100f);

        double recebido = asset.vender(2f);

        assertEquals(2f, recebido, 0.0001);
        assertEquals(8f, asset.getQuantidade(), 0.0001);
        assertEquals(80f, asset.getValorAtual(), 0.0001);
    }

    @Test
    void editarRedefinesQuantityAndInvestedBaseValues() {
        TestAsset asset = new TestAsset("TEST", 100f);

        asset.editar(5f, 50f);

        assertEquals(5f, asset.getQuantidade(), 0.0001);
        assertEquals(50f, asset.getInvestido(), 0.0001);
        assertEquals(50f, asset.getValorAtual(), 0.0001);
    }

    @Test
    void calcularRentabilidadeReturnsZeroWhenNoGain() {
        TestAsset asset = new TestAsset("TEST", 100f);

        assertEquals(0.0, asset.calcularVariaçãoMonetaria(), 0.0001);
        assertEquals(0.0, asset.calcularRentabilidade(), 0.0001);
    }
}
