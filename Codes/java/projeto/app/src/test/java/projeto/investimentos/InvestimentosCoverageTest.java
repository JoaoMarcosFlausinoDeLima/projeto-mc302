package projeto.investimentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import projeto.excecoes.InvalidAssetException;

class InvestimentosCoverageTest {

    private static final class TestAsset extends FinancialAsset {
        TestAsset(String nome, float dinheiro)  throws InvalidAssetException{
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
    void acaoExposesMetadataAndRendersDividendIncome()  throws InvalidAssetException{
        Açao acao = new Açao("PETR4", 100f);
        acao.setPrecoAtual(10.5);
        acao.setQuantidade(4f);
        acao.setInvestido(42f);
        acao.dividendo_anual = 1.5f;
        acao.empresa = "Petrobras";
        acao.pl = 5.5f;
        acao.pvp = 1.2f;
        acao.vpa = 3.0f;
        acao.valorMercado = 120000.0;
        acao.min52 = 8.0f;
        acao.max52 = 12.0f;

        assertEquals("Ação", acao.getTipoNome());
        assertEquals("Petrobras", acao.getEmpresa());
        assertEquals(5.5f, acao.getPL(), 0.0001);
        assertEquals(1.2f, acao.getPVP(), 0.0001);
        assertEquals(3.0f, acao.getVPA(), 0.0001);
        assertEquals(120000.0, acao.getValorMercado(), 0.0001);
        assertEquals(8.0f, acao.getMin52(), 0.0001);
        assertEquals(12.0f, acao.getMax52(), 0.0001);
        assertEquals(6.0, acao.render(), 0.0001);

        acao.resumo();
    }

    @Test
    void fiiExposesMetadataAndCalculatesDividendIncome()  throws InvalidAssetException{
        Fii fii = new Fii("HGLG11", 200f);
        fii.setPrecoAtual(4.0);
        fii.setQuantidade(5f);
        fii.setInvestido(20f);
        fii.dividendo_anual = 0.5f;
        fii.nomeFundo = "HGLG11";
        fii.segmento = "Logística";
        fii.gestao = "Ativa";
        fii.receita = 10.5f;
        fii.valorMercado = 500000.0;
        fii.min52 = 3.0f;
        fii.max52 = 5.0f;
        fii.volumeMedio = 1000;

        assertEquals("FII", fii.getTipoNome());
        assertEquals("HGLG11", fii.getNomeFundo());
        assertEquals("Logística", fii.getSegmento());
        assertEquals("Ativa", fii.getGestao());
        assertEquals(10.5f, fii.getReceita(), 0.0001);
        assertEquals(500000.0, fii.getValorMercado(), 0.0001);
        assertEquals(3.0f, fii.getMin52(), 0.0001);
        assertEquals(5.0f, fii.getMax52(), 0.0001);
        assertEquals(1000, fii.getVolumeMedio());
        assertEquals(2.5, fii.render(), 0.0001);

        fii.resumo();
    }

    @Test
    void criptomoedaProvidesResumoAndTypeWithoutNetwork()  throws InvalidAssetException {
        Criptomoeda criptomoeda = new Criptomoeda("bitcoin", 150f);
        criptomoeda.setPrecoAtual(50.0);
        criptomoeda.setQuantidade(3f);
        criptomoeda.setInvestido(150f);

        assertEquals("Criptomoeda", criptomoeda.getTipoNome());
        assertEquals(0.0, criptomoeda.render(), 0.0001);
        assertEquals(150.0, criptomoeda.getValorAtual(), 0.0001);

        criptomoeda.resumo();
    }

    @Test
    void fundoDeInvestimentoCoversResumoAndEditing()  throws InvalidAssetException{
        FundoDeInvestimento fundo = new FundoDeInvestimento("Multimercado", 200f,
                0.01f, 0.02f, "Multimercado", "IBOV", 15f, 5);

        fundo.editar_fundo(20f, 10f);
        fundo.setInvestido(100f);

        assertEquals("Fundo de Investimento", fundo.getTipoNome());
        assertEquals(200f, fundo.getValorAtual(), 0.0001);
        assertEquals(10f, fundo.getQuantidade(), 0.0001);
        assertEquals(100f, fundo.getInvestido(), 0.0001);

        fundo.resumo();
    }

    @Test
    void tituloRendaFixaCoversCompraAtualizacaoAndResumo() throws Exception {
        int[] dataCompra = {1, 1, 2025};
        int[] dataVencimento = {1, 1, 2027};
        int[] ultimaAtualizacao = {1, 1, 2025};

        TituloRendaFixa titulo = new TituloRendaFixa("Tesouro", 1000f,
                "pre", "SELIC", 0.05f,
                dataCompra, dataVencimento, ultimaAtualizacao);

        titulo.comprar(100f);
        assertEquals(1100f, titulo.getInvestido(), 0.0001);
        assertEquals(1100f, titulo.getValorAtual(), 0.0001);
        assertEquals("Título de Renda Fixa", titulo.getTipoNome());

        Field ultimaAtualizacaoField = TituloRendaFixa.class.getDeclaredField("ultima_atu");
        ultimaAtualizacaoField.setAccessible(true);
        ultimaAtualizacaoField.set(titulo, LocalDate.now().minusMonths(1));

        Field dataVencimentoField = TituloRendaFixa.class.getDeclaredField("data_de_vencimento");
        dataVencimentoField.setAccessible(true);
        dataVencimentoField.set(titulo, LocalDate.now().plusMonths(2));

        titulo.atualizarInformacoes();
        titulo.resumo();

        TituloRendaFixa tituloPos = new TituloRendaFixa("Tesouro", 1000f,
                "pos", "IPCA", 0.02f,
                dataCompra, dataVencimento, ultimaAtualizacao);
        tituloPos.setInvestido(100f);
        tituloPos.setPrecoAtual(100f);
        tituloPos.atualizarInformacoes();
        assertNotNull(tituloPos.getTipoNome());
    }
}
