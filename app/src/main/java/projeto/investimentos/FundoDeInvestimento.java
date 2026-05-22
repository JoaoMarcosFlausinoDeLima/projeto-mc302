package projeto.investimentos;

public class FundoDeInvestimento  extends Investimento{
    
    public FundoDeInvestimento(String nome, float dinheiro){
        super(nome, dinheiro);
    }

    public float render(int data[]){

        if(this.data[1] == data[1] && this.data[2] == data[2]){
            float rendimentoBruto = this.dividendo * this.quantidade;
            return rendimentoBruto;
        }
        return 0;
    }

}
