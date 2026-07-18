package projeto.servicos;

import projeto.relatorios.RelatorioGeral;
import projeto.relatorios.RelatorioPorTipo;
import projeto.relatorios.Report;
import projeto.sistemas.Carteira;

/**
 * Serviço responsável por criar os relatórios da carteira.
 *
 * <p>Centraliza a geração de relatórios, isolando a interface gráfica e o
 * restante do sistema dos detalhes de construção de cada relatório.</p>
 */
public class ReportService {

    /**
     * Cria um relatório geral da carteira.
     *
     * @param carteira carteira a ser analisada
     * @return relatório geral pronto para gerar/exportar
     */
    public Report gerarRelatorioGeral(Carteira carteira) {
        return new RelatorioGeral(carteira);
    }

    /**
     * Cria um relatório agrupado por tipo de ativo.
     *
     * @param carteira carteira a ser analisada
     * @return relatório por tipo pronto para gerar/exportar
     */
    public Report gerarRelatorioPorTipo(Carteira carteira) {
        return new RelatorioPorTipo(carteira);
    }
}
