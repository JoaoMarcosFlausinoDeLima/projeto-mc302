package projeto.investimentos;

import java.util.Calendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Classe base para ativos financeiros que guarda informações comuns a todos os investimentos.
 *
 * <p>Esta classe representa atributos básicos de um ativo: nome, quantidade, preço atual,
 * valor investido e valor de mercado calculado. Também define operações gerais como comprar,
 * vender e calcular a variação monetária.</p>
 */
public abstract class FinancialAsset {
    /**
     * Nome ou código do ativo financeiro.
     * Exemplo: ticker de ação ou símbolo de FII.
     */
    protected String nome;

    /**
     * Quantidade de unidades do ativo possuídas pelo usuário.
     * Para ações, é o número de ações; para FIIs, é o número de cotas.
     */
    protected float quantidade;

    /**
     * Total investido no ativo em moeda local.
     * Usado para comparar com o valor de mercado atual.
     */
    protected float investido;

    /**
     * Preço atual por unidade do ativo.
     * Representa a cotação do ativo no momento da atualização.
     */
    protected double preco_atual;

    /**
     * Valor total de mercado das unidades em carteira.
     * Calculado como quantidade * preço atual.
     */
    protected double dinheiro_total;

    /**
     * Data usada para cálculos de rendimento ou filtros, no formato [dia, mês, ano].
     */
    protected int data[] = {0,0,0};

    /**
     * Setor econômico ou categoria do ativo.
     * Exemplo: "Imobiliário", "Energia" ou "Tecnologia".
     */
    protected String setor;
    
    /**
     * Inicializa um ativo financeiro com nome e valor investido, e busca dados externos.
     *
     * @param nome nome ou código do ativo
     * @param dinheiro valor investido inicialmente no ativo
     */
    public FinancialAsset(String nome,Float dinheiro){
        this.nome = nome;
        atualizarInformacoes();
        comprar(dinheiro);
    }

    /**
     * Retorna o nome ou código do ativo.
     *
     * @return nome do ativo
     */
    public String getNome(){
        return nome;
    }

    /**
     * Retorna o preço atual da unidade do ativo.
     *
     * @return cotação atual do ativo
     */
    public double getPrecoAtual(){
        return preco_atual;
    }
    
    /**
     * Retorna a quantidade de unidades do ativo em posse.
     *
     * @return quantidade de ações ou cotas
     */
    public float getQuantidade(){
        return quantidade;
    }

    /**
     * Retorna o total investido no ativo.
     *
     * @return valor investido em reais
     */
    public double getInvestido(){
        return investido;
    }

    /**
     * Atualiza o valor total do ativo com base na quantidade e no preço atual.
     */
    private void atualizarDinheiroTotal(){
        this.dinheiro_total = quantidade*preco_atual;
    }

    

    /**
     * Compra unidades do ativo usando o valor informado.
     *
     * @param dinheiro valor em reais utilizado para a compra
     */
    public void comprar(float dinheiro){
        quantidade += dinheiro/preco_atual;
        investido += dinheiro;
    }

    /**
     * Vende a quantidade informada de unidades do ativo.
     *
     * @param quantidade quantidade de unidades a vender
     * @return valor recebido pela venda
     */
    public double vender(float quantidade){
        this.quantidade -= quantidade;
        this.dinheiro_total -= quantidade*preco_atual;
        return preco_atual*quantidade;
    }

    /**
     * Calcula a variação monetária do ativo, ou seja, o lucro ou prejuízo atual, com relação ao preço.
     *
     * @return valor da variação monetária em reais
     */
    public double variacaoMonetaria(){
        return (preco_atual*quantidade - investido);
    }

    /**
     * Calcula o rendimento do ativo para o período informado.
     * A implementação específica depende do tipo de ativo.
     *
     * @param data array com [dia, mês, ano]
     * @return valor do rendimento no período informado
     */
    public abstract double render(int data[]);

    /**
     * Atualiza os dados específicos do ativo a partir de uma fonte externa.
     */
    public abstract void atualizarInformacoes();
        
    public abstract void resumo();
}
