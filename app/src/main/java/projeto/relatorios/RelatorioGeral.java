package projeto.relatorios;

import java.util.Locale;

import projeto.sistemas.Carteira;

/**
 * Relatório com a visão geral da carteira: total investido, valor atual,
 * lucro ou prejuízo e rentabilidade geral.
 */
public class RelatorioGeral extends Report {

    public RelatorioGeral(Carteira carteira) {
        super(carteira);
    }

    @Override
    public String gerar() {
        StringBuilder sb = new StringBuilder();
        carteira.atualizarInformacoes();
        sb.append("===== RELATÓRIO GERAL DA CARTEIRA =====\n");
        sb.append("Data: ").append(dataGeracao).append("\n");
        sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                "Quantidade de ativos: %d%n", carteira.getInvestimentos().size()));
        sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                "Total investido: R$ %.2f%n", carteira.calcularTotalInvestido()));
        sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                "Valor atual: R$ %.2f%n", carteira.calcularValorTotal()));
        sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                "Lucro/Prejuízo: R$ %.2f%n", carteira.calcularVariacaoTotal()));
        sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                "Rentabilidade: %.2f%%%n", carteira.calcularRentabilidadeGeral()));
        return sb.toString();
    }
}
