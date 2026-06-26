package projeto.investimentos;

import projeto.interfaces.Calculavel;


/**
 * Classe base para ativos financeiros que guarda informações comuns a todos os investimentos.
 *
 * <p>Esta classe representa atributos básicos de um ativo: nome, quantidade, preço atual,
 * valor investido e valor de mercado calculado. Também define operações gerais como comprar,
 * vender e calcular a variação monetária.</p>
 */
public abstract class FinancialAsset implements Calculavel {

    protected int tipo;
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
    //protected int data[] = {0,0,0};

    /**
     * Inicializa um ativo financeiro com nome e valor investido, e busca dados externos.
     *
     * @param nome nome ou código do ativo
     * @param dinheiro valor investido inicialmente no ativo
     */
    public FinancialAsset(String nome,Float dinheiro){
        this.nome = nome;
        this.investido = 0;
        this.dinheiro_total = 0;
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
     * Retorna o valor de mercado atual do ativo (quantidade x preço atual).
     *
     * @return valor atual da posição em reais
     */
    public double getValorAtual(){
        return dinheiro_total;
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
        if (preco_atual <= 0){
            System.err.println("Erro: preço atual do ativo não está definido.");
            quantidade += 0;
            investido += 0;
            atualizarDinheiroTotal();
            return;
        }
        quantidade += dinheiro/preco_atual;
        investido += dinheiro;
        atualizarDinheiroTotal();
    }

    public void setPrecoAtual(double novoPreco){
        this.preco_atual = novoPreco;
        atualizarDinheiroTotal();
    }

    public void setQuantidade(float novaQuantidade){
        this.quantidade = novaQuantidade;
        atualizarDinheiroTotal();
    }

    public void setInvestido(float novoInvestido){
        this.investido = novoInvestido;
        atualizarDinheiroTotal();
    }

    /**
     * Vende a quantidade informada de unidades do ativo.
     *
     * @param quantidade quantidade de unidades a vender
     * @return valor recebido pela venda
     */
    public double vender(float quantidade){
        this.quantidade -= quantidade;
        atualizarDinheiroTotal();
        return preco_atual*quantidade;
    }

    /**
     * Edita a posição do ativo, redefinindo a quantidade e o valor investido.
     *
     * @param novaQuantidade nova quantidade de unidades
     * @param novoInvestido novo valor investido em reais
     */
    public void editar(float novaQuantidade, float novoInvestido){
        this.quantidade = novaQuantidade;
        this.investido = novoInvestido;
        atualizarDinheiroTotal();
    }

    /**
     * Calcula a variação monetária do ativo, ou seja, o lucro ou prejuízo atual, com relação ao preço.
     *
     * @return valor da variação monetária em reais
     */
    @Override
    public double calcularVariaçãoMonetaria(){
        return (dinheiro_total - investido);
    }

    /**
     * Calcula a rentabilidade percentual do ativo em relação ao valor investido.
     *
     * @return rentabilidade em porcentagem; 0 se nada foi investido
     */
    @Override
    public double calcularRentabilidade(){
        if(investido == 0){
            return 0;
        }
        return (calcularVariaçãoMonetaria() / investido) * 100;
    }

    /**
     * Calcula o rendimento do ativo para o período informado.
     * A implementação específica depende do tipo de ativo.
     *
     * @param data array com [dia, mês, ano]
     * @return valor do rendimento no período informado
     */
    public abstract double render();

    /**
     * Atualiza os dados específicos do ativo a partir de uma fonte externa.
     */
    public abstract void atualizarInformacoes();
        
    public abstract void resumo();

    /**
     * Retorna o rótulo legível do tipo do ativo (ex.: "Ação", "FII").
     *
     * @return nome do tipo do ativo
     */
    public abstract String getTipoNome();

}
