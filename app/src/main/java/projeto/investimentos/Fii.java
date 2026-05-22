package projeto.investimentos;

public class Fii extends Investimento{

    public Fii(String nome,float dinheiro){
        super(nome, dinheiro);
        
    }

    public float render(int data[]){
        if(this.data[2] == data[2]){
            return this.dividendo*this.quantidade;
        }

        return 0;
        
    }
}
