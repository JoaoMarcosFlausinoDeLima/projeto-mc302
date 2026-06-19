package projeto.sistemas;
import java.util.ArrayList;
import java.util.List;

import projeto.investimentos.FinancialAsset;

public class Carteira {
    private List<FinancialAsset> investimentos = new ArrayList<>();

    public void cadastra(String tipo, String nome, int dinheiro){
        switch(tipo){
            case "Ação":
                investimentos.add(new projeto.investimentos.Açao(nome, dinheiro));
                break;
            case "Fii":
                investimentos.add(new projeto.investimentos.Fii(nome, dinheiro));
                break;
            case "FundoDeInvestimento":
                investimentos.add(new projeto.investimentos.FundoDeInvestimento(nome, dinheiro));
                break;
            case "TituloRendaFixa":
                investimentos.add(new projeto.investimentos.TituloRendaFixa(nome, dinheiro));
                break;
            case "Criptomoeda":
                investimentos.add(new projeto.investimentos.Criptomoeda(nome, dinheiro));
                break;
        }
    }

    public void compra(String nome, int dinheiro){
        for(int i = 0; i < investimentos.size(); i ++){
            if(investimentos.get(i).getNome().equals(nome)){
                investimentos.get(i).comprar(dinheiro);
            }
        }
        
        // criar uma função para descobrir o tipo de investimento so pelo nome;
        cadastra("temp", nome, dinheiro);
    }

    public void remove(String nome){
        for(int i = 0; i < investimentos.size(); i ++){
            if(investimentos.get(i).getNome().equals(nome)){
                investimentos.remove(i);
                return;
            }
        }
        
    }
    public void resumo(String nome){
        for(int i = 0; i < investimentos.size(); i ++){
            if(investimentos.get(i).getNome().equals(nome)){
                investimentos.get(i).resumo();
                return;
            }
        }
        
    }

    public void variacao(String nome){
        for(int i = 0; i < investimentos.size(); i ++){
            if(investimentos.get(i).getNome().equals(nome)){
                investimentos.get(i).variacaoMonetaria();
                return;
            }
        }
        
    }

    public float vender(String nome, int quantidade){
        for(int i = 0; i < investimentos.size(); i ++){
            if(investimentos.get(i).getNome().equals(nome)){
                return (float) investimentos.get(i).vender(quantidade);
            }
        }
        return 0;
    }
    
    public void atualizarInformacoes(){
        for(int i = 0; i < investimentos.size(); i ++){
            investimentos.get(i).atualizarInformacoes();
        }
    }

    public float render(){
        float total = 0;
        for(int i = 0; i < investimentos.size(); i ++){
            total += investimentos.get(i).render();
        }
        return total;
    }

     public float getPrecoAtivo(String nome){
        for(int i = 0; i < investimentos.size(); i ++){
            System.out.println(investimentos.get(i).getNome());
            if(investimentos.get(i).getNome().equals(nome)){
                return (float) investimentos.get(i).getPrecoAtual();
            }
        }
        return 0;   

    }


    public List<FinancialAsset> getInvestimentos() {
        return investimentos;
    }
}
