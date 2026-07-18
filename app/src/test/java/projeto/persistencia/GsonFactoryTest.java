package projeto.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import projeto.investimentos.Açao;
import projeto.investimentos.Criptomoeda;
import projeto.investimentos.Fii;
import projeto.investimentos.FinancialAsset;

class GsonFactoryTest {

    @Test
    void shouldDeserializeFiiAndCriptomoedaAndAcaoByTipoField() {
        Gson gson = GsonFactory.criar();

        FinancialAsset fii = gson.fromJson(
                "{\"tipo\":1,\"nome\":\"HGLG11\",\"quantidade\":10,\"investido\":1000,\"preco_atual\":10,\"dinheiro_total\":100}",
                FinancialAsset.class);
        assertTrue(fii instanceof Fii);
        assertEquals("FII", fii.getTipoNome());

        FinancialAsset crypto = gson.fromJson(
                "{\"tipo\":2,\"nome\":\"bitcoin\",\"quantidade\":3,\"investido\":150,\"preco_atual\":50,\"dinheiro_total\":150}",
                FinancialAsset.class);
        assertTrue(crypto instanceof Criptomoeda);
        assertEquals("Criptomoeda", crypto.getTipoNome());

        FinancialAsset acao = gson.fromJson(
                "{\"tipo\":0,\"nome\":\"PETR4\",\"quantidade\":10,\"investido\":100,\"preco_atual\":10,\"dinheiro_total\":100}",
                FinancialAsset.class);
        assertTrue(acao instanceof Açao);
        assertEquals("Ação", acao.getTipoNome());
    }
}
