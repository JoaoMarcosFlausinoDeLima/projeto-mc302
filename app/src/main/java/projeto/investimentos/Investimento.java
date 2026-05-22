package projeto.investimentos;

public abstract class Investimento {
    protected String nome;
    protected int quantidade;
    protected float investido;
    protected float preco_atual;
    protected float dinheiro_total;
    protected float dividendo;
    protected int data[];
    protected String setor;
    
    public Investimento(String nome,Float dinheiro){
        this.nome = nome;
        this.investido = dinheiro;
    }

    // criar função que usa api para pegar informaçoes dos investimentos 

    public String getNome(){
        return nome;
    }
    public float getPrecoAtual(){
        return preco_atual;
    }
    public float getDividendo(){
        return dividendo;
    }
    public int getQuantidade(){
        return quantidade;
    }
    public float getInvestido(){
        return investido;
    }

    public void comprar(int dinheiro){
        quantidade += (int) dinheiro/preco_atual;
    }
    public float vender(int quantidade){
        this.quantidade -= quantidade;
        this.dinheiro_total -= quantidade*preco_atual;
        return preco_atual*quantidade;
    }

    public abstract float render(int data[]);



}
