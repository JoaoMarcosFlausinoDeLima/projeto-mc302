package projeto.investimentos;

public abstract class Investimento {
    protected String nome;
    protected int quantidade;
    protected float investido;
    protected float preco_atual;
    protected float dinheiro_total;
    protected float dividendo;
    protected int data[];
    
    public Investimento(String nome,float preco_atual, float dividendo, int quantidade, float investido,int data[]){
        this.nome = nome;
        this.preco_atual = preco_atual;
        this.dividendo = dividendo;
        this.quantidade = quantidade;
        this.investido = investido;
        this.data = data;
        dinheiro_total = quantidade*preco_atual;
        
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
