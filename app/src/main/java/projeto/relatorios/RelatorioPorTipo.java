package projeto.relatorios;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import projeto.investimentos.FinancialAsset;
import projeto.sistemas.Carteira;

/**
 * Relatório que agrupa os ativos da carteira por tipo (Ação, FII, Criptomoeda,
 * Título de Renda Fixa, Fundo de Investimento), somando investido, valor atual
 * e variação de cada grupo.
 */
public class RelatorioPorTipo extends Report {

    public RelatorioPorTipo(Carteira carteira) {
        super(carteira);
    }

    @Override
    public String gerar() {
        carteira.atualizarInformacoes();
        // Agrupa os totais por tipo de ativo, preservando a ordem de aparição.
        Map<String, double[]> grupos = new LinkedHashMap<>();
        for (FinancialAsset ativo : carteira.getInvestimentos()) {
            String tipo = ativo.getTipoNome();
            double[] acumulado = grupos.computeIfAbsent(tipo, k -> new double[3]);
            acumulado[0] += 1;                      // quantidade de ativos
            acumulado[1] += ativo.getInvestido();   // investido
            acumulado[2] += ativo.getValorAtual();  // valor atual
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATÓRIO POR TIPO DE ATIVO =====\n");
        sb.append("Data: ").append(dataGeracao).append("\n");

        if (grupos.isEmpty()) {
            sb.append("Nenhum ativo cadastrado.\n");
            return sb.toString();
        }

        for (Map.Entry<String, double[]> grupo : grupos.entrySet()) {
            double[] v = grupo.getValue();
            double variacao = v[2] - v[1];
            sb.append(String.format(Locale.forLanguageTag("pt-BR"),
                    "[%s] ativos: %d | investido: R$ %.2f | atual: R$ %.2f | variação: R$ %.2f%n",
                    grupo.getKey(), (int) v[0], v[1], v[2], variacao));
        }
        return sb.toString();
    }
}
