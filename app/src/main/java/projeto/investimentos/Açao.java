package projeto.investimentos;

public class Açao extends Investimento{

    public Açao(String nome,float dinheiro){
        super(nome, dinheiro);
        
    }

    public float render(int data[]){
        if(this.data[1] == data[1] && this.data[2] == data[2]){
            return this.dividendo*this.quantidade;
        }

        return 0;
        
    }
}
