package projeto.investimentos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import projeto.excecoes.InvalidAssetException;

class InvestimentoSubclassTest {

    @Test
    void acaoShouldReturnCorrectTipoNomeAndRender()  throws InvalidAssetException{
        Açao acao = new Açao("PETR4", 100f);
        acao.preco_atual = 10.0;
        acao.comprar(100f);
        acao.dividendo_anual = 1.0f;

        assertEquals("Ação", acao.getTipoNome());
        assertEquals(10.0, acao.getPrecoAtual(), 0.0001);
    }

    @Test
    void fiiShouldReturnCorrectTipoNomeAndRender()  throws InvalidAssetException {
        Fii fii = new Fii("HGLG11", 200f);
        fii.setPrecoAtual(4.0);
        fii.setQuantidade(5f);
        fii.dividendo_anual = 0.5f;

        assertEquals("FII", fii.getTipoNome());
        assertEquals(2.5, fii.render(), 0.0001);
    }

    @Test
    void criptomoedaShouldReturnCorrectTipoName()  throws InvalidAssetException {
        Criptomoeda crypto = new Criptomoeda("bitcoin", 150f);
        crypto.setPrecoAtual(50.0);
        crypto.comprar(150f);

        assertEquals("Criptomoeda", crypto.getTipoNome());
        assertEquals(3f, crypto.getQuantidade(), 0.1);
    }

    @Test
    void tituloRendaFixaShouldAllowCompraAndProvideTipoName()  throws InvalidAssetException {
        int[] dataCompra = {1, 1, 2025};
        int[] dataVencimento = {1, 1, 2027};
        int[] ultimaAtualizacao = {1, 1, 2025};

        TituloRendaFixa titulo = new TituloRendaFixa("Tesouro", 1000f,
                "pre", "SELIC", 0.05f,
                dataCompra, dataVencimento, ultimaAtualizacao);

        assertEquals("Título de Renda Fixa", titulo.getTipoNome());

        titulo.comprar(100f);
        assertEquals(1100f, titulo.getValorAtual(), 0.0001);
    }
}
