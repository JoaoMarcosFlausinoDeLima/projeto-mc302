package projeto.investimentos;

import projeto.excecoes.*;


public class FundoDeInvestimento  extends FinancialAsset{
    
    private String tipo_fundo; //# Opções: "Renda Fixa", "Acoes", "Multimercado", "Cambial"
    private String benchmark; //# O índice de referência que o gestor tenta bater
    private float taxa_administracao; //# Percentual cobrado pelo gestor do fundo
    private float taxa_performance; //# Percentual cobrado sobre o que exceder o benchmark
  
    private float pre;
    
    public FundoDeInvestimento(String nome, float dinheiro,float taxa_administracao,float taxa_performance,String tipo,String benchmark,float cotacao,int quantidade_de_cotas) throws InvalidAssetException{
        this.pre = cotacao;
        super(nome, dinheiro);
        this.quantidade = quantidade_de_cotas;
        this.benchmark = benchmark;
        this.tipo_fundo = tipo;
        this.dinheiro_total = quantidade_de_cotas*preco_atual;
        this.taxa_administracao = taxa_administracao;
        this.taxa_performance = taxa_performance;
    }



    public void atualizarInformacoes(){
        this.preco_atual = pre;
    }

    public void editar_fundo(Float novaCotacao, Float novaQuantidade){
        if(novaCotacao <= 0 || novaQuantidade < 0){
            throw new IllegalArgumentException("Cotação e quantidade não podem ser negativas.");
        }
        this.preco_atual = novaCotacao;
        this.quantidade = novaQuantidade;
        this.dinheiro_total = novaCotacao*novaQuantidade;
    }
    
    public double render(){
        return 0;
    }

    @Override
    public String getTipoNome(){
        return "Fundo de Investimento";
    }

    public void resumo(){
        System.out.println("Fundo de Investimento: " + this.nome);
        System.out.println("Taxa de administração: " + this.taxa_administracao);
        System.out.println("Taxa de performance: " + this.taxa_performance);
        System.out.println("Tipo do fundo: " + this.tipo_fundo);
        System.out.println("Benchmark: " + this.benchmark);
        System.out.println("Cotação atual: R$ " + this.preco_atual);
        System.out.println("Quantidade de cotas: " + this.quantidade);
        System.out.println("Valor investido: R$ " + this.investido);
        System.out.println("Variação monetária: R$ " + calcularVariaçãoMonetaria());
    }
}
