package projeto.interfaces;

/**
 * Contrato para ativos que possuem métricas de desempenho financeiro.
 *
 * <p>Toda classe que representa um ativo deve ser capaz de informar o quanto
 * variou em valor e qual a sua rentabilidade percentual.</p>
 */
public interface Calculavel {

    /**
     * Calcula a variação monetária do ativo (lucro ou prejuízo em reais).
     *
     * @return diferença entre o valor de mercado atual e o valor investido
     */
    double calcularVariaçãoMonetaria();

    /**
     * Calcula a rentabilidade percentual do ativo em relação ao valor investido.
     *
     * @return rentabilidade em porcentagem
     */
    double calcularRentabilidade();
}
