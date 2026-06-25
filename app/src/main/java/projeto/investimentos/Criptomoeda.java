package projeto.investimentos;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Criptomoeda extends FinancialAsset {
    public Criptomoeda(String nome, float dinheiro){
        super(nome, dinheiro);
        tipo = 2;
    }

    public void atualizarInformacoes(){
        try{
            String link = "https://coinmarketcap.com/currencies/" + this.nome;
            Document doc = Jsoup.connect(link).get();
            Element preco = doc.selectFirst("span[data-test=text-cdp-price-display]");
            this.preco_atual = Float.parseFloat(preco.text().replace("$", ""));
            
         } catch (Exception e) {
            System.out.println("Erro ao atualizar informações da cripito: " + e.getMessage());
        }
    }

    public double render(){
        return 0;
    }

    @Override
    public String getTipoNome(){
        return "Criptomoeda";
    }

    public void resumo(){
        System.out.println("Criptomoeda: " + this.nome);
        System.out.println("Preço atual: R$ " + this.preco_atual);
        System.out.println("Quantidade: " + this.quantidade);
        System.out.println("Valor investido: R$ " + this.investido);
        System.out.println("Variação monetária: R$ " + calcularVariaçãoMonetaria());
    }
}
