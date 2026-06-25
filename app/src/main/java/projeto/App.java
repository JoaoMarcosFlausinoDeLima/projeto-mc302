package projeto;

import projeto.excecoes.PersistenceException;
import projeto.servicos.ReportService;
import projeto.sistemas.Carteira;

public class App {

    public static void main(String[] args) {
        Carteira carteira = new Carteira();

        try {
            carteira.carregar();
        } catch (PersistenceException e) {
            System.out.println(e.getMessage());
        }

        carteira.atualizarInformacoes();
        carteira.resumo("PETR4");
        System.out.println("");
        carteira.resumo("HGLG11");
        System.out.println("");
        carteira.resumo("BNB");

        System.out.println();
        ReportService relatorios = new ReportService();
        System.out.println(relatorios.gerarRelatorioGeral(carteira).gerar());
        System.out.println(relatorios.gerarRelatorioPorTipo(carteira).gerar());

        try {
            carteira.salvar();
        } catch (PersistenceException e) {
            System.out.println(e.getMessage());
        }
    }
}
