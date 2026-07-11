package projeto.relatorios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import projeto.investimentos.FinancialAsset;
import projeto.sistemas.Carteira;
import projeto.excecoes.*;

class RelatorioTest {

    private static final class TestAsset extends FinancialAsset {
        private final String tipoNome;

        TestAsset(String nome, float dinheiro, String tipoNome) throws InvalidAssetException{
            super(nome, dinheiro);
            this.tipoNome = tipoNome;
        }

        @Override
        public void atualizarInformacoes() {
            this.preco_atual = 10.0;
            this.dinheiro_total = quantidade * preco_atual;
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
            return tipoNome;
        }
    }

    @Test
    void relatorioGeralShouldSummarizeCarteiraValues() throws Exception {
        Carteira carteira = new Carteira();
        TestAsset ativo1 = new TestAsset("PETR4", 100f, "Ação");
        TestAsset ativo2 = new TestAsset("HGLG11", 50f, "FII");
        carteira.getInvestimentos().add(ativo1);
        carteira.getInvestimentos().add(ativo2);

        RelatorioGeral relatorio = new RelatorioGeral(carteira);
        String conteudo = relatorio.gerar();

        assertTrue(conteudo.contains("RELATÓRIO GERAL DA CARTEIRA"));
        assertTrue(conteudo.contains("Quantidade de ativos: 2"));
        assertTrue(conteudo.contains("Total investido: R$ 150,00"));
        assertTrue(conteudo.contains("Valor atual: R$ 150,00"));
        assertTrue(conteudo.contains("Lucro/Prejuízo: R$ 0,00"));
    }

    @Test
    void relatorioPorTipoShouldGroupAssetsByType()  throws InvalidAssetException{
        Carteira carteira = new Carteira();
        carteira.getInvestimentos().add(new TestAsset("PETR4", 100f, "Ação"));
        carteira.getInvestimentos().add(new TestAsset("HGLG11", 50f, "FII"));
        carteira.getInvestimentos().add(new TestAsset("BTC", 80f, "Ação"));

        RelatorioPorTipo relatorio = new RelatorioPorTipo(carteira);
        String conteudo = relatorio.gerar();

        assertTrue(conteudo.contains("RELATÓRIO POR TIPO DE ATIVO"));
        assertTrue(conteudo.contains("[Ação]"));
        assertTrue(conteudo.contains("[FII]"));
        assertTrue(conteudo.contains("ativos: 2"));
    }

    @Test
    void exportarShouldWriteReportToFile() throws Exception {
        Path caminho = Files.createTempFile("relatorio", ".txt");
        Carteira carteira = new Carteira();
        carteira.getInvestimentos().add(new TestAsset("PETR4", 100f, "Ação"));

        RelatorioGeral relatorio = new RelatorioGeral(carteira);
        relatorio.exportar(caminho.toString());

        String texto = Files.readString(caminho);
        assertTrue(texto.contains("RELATÓRIO GERAL DA CARTEIRA"));
        assertTrue(texto.contains("Quantidade de ativos: 1"));
        assertTrue(texto.contains("Total investido: R$ 100,00"));
        Files.deleteIfExists(caminho);
    }
}
