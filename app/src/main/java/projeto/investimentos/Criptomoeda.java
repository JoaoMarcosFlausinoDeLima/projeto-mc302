package projeto.investimentos;

public class Criptomoeda extends Investimento {
    public Criptomoeda(String nome, float dinheiro){
        super(nome, dinheiro);
        this.setor = "Criptoativos";
    }

    public float render(int data[]){
        return 0;
    }
}
