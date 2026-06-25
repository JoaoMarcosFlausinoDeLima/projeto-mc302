package projeto.interfaces;

import projeto.investimentos.FinancialAsset;

/**
 * Contrato para sistemas capazes de gerar alertas sobre o desempenho de um ativo.
 */
public interface Alertavel {

    /**
     * Analisa um ativo e retorna um alerta textual sobre o seu desempenho.
     *
     * @param ativo ativo financeiro a ser analisado
     * @return mensagem de alerta (ex.: "ativo em alta", "ativo em queda")
     */
    String gerarAlerta(FinancialAsset ativo);
}
