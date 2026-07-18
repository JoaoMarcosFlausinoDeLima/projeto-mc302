package projeto;

import projeto.servicos.ReportService;
import projeto.sistemas.Carteira;
import projeto.excecoes.InvalidAssetException;

/**
 * Demonstração de uso da aplicação de carteira de investimentos.
 *
 * <p>Este ponto de entrada cria uma carteira, cadastra um ativo e gera
 * relatórios de desempenho no console.</p>
 */
public class App {

    public static void main(String[] args) {
        Carteira carteira = new Carteira();

        int data_compra[] = {1, 1, 2023};
        int data_vencimento[] = {1, 1, 2026};
        int data_de_ultima_atualizacao[] = {1, 1, 2023};
        try{
            //carteira.cadastra("Ação", "PETR4", 1000);
            //carteira.cadastra("Fii", "HGLG11", 1000);
            carteira.cadastra("Criptomoeda", "PAXG", 1000);
            //carteira.cadastra_titulo("teste", 1000, "pos", "SELIC", 0, data_compra, data_vencimento, data_de_ultima_atualizacao);
            //carteira.cadastra_fundo("teste2", 1000, 0.01f, 0.02f, "tipo", "benchmark", 10, 100);

        } catch (InvalidAssetException e){
            System.out.println("Erro ao cadastrar ativo: " + e.getMessage());
        }

       
        
       
        System.out.println();
        try{
            carteira.atualizarInformacoes();
        }catch (InvalidAssetException e){
            System.out.println("Erro ao atualizar informações: " + e.getMessage());
        }

        carteira.resumos();

        ReportService relatorios = new ReportService();
        
        System.out.println(relatorios.gerarRelatorioGeral(carteira).gerar());
        System.out.println(relatorios.gerarRelatorioPorTipo(carteira).gerar());

        
    }
}
