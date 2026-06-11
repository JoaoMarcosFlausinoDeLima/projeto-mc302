package projeto.investimentos;

import java.util.Calendar;


import java.util.Calendar;

public class FundoDeInvestimento  extends FinancialAsset{
    protected float dividendo;
    
    public FundoDeInvestimento(String nome, float dinheiro){
        super(nome, dinheiro);
    }

    public float getDividendo(){
        return dividendo;
    }

    public void atualizarInformacoes(){
        try{
            // implementar para fundo de investimento

        } catch (Exception e){
            System.out.println("Erro ao atu");
        }
    }
    
    public double render(int data[]){

        if(this.data[1] == data[1] && this.data[2] == data[2]){
            double rendimentoBruto = this.dividendo * this.quantidade;
            return rendimentoBruto;
        }
        return 0;
    }
    public void resumo(){}
}
