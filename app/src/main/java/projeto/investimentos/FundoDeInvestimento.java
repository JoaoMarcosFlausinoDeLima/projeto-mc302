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
    
    public double render(){
        return 0;
    }
    public void resumo(){}
}
