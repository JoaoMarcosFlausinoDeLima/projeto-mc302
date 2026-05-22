package projeto.investimentos;

public class TituloRendaFixa extends Investimento {

    public TituloRendaFixa(String nome, float dinheiro){
        super(nome, dinheiro);
    }

    public float render(int data[]){
        this.preco_atual = preco_atual*(1+this.dividendo);
        return 0;
    }
}
