package projeto.sistemas;
import java.util.ArrayList;
import java.util.List;

import projeto.investimentos.Investimento;

public class Carteira {
    List<Investimento> investimentos = new ArrayList<>();

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

    public List<Investimento> getInvestimentos() {
        return investimentos;
    }
}
