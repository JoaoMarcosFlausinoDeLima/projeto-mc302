package projeto.investimentos;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Criptomoeda extends FinancialAsset {
    public Criptomoeda(String nome, float dinheiro){
        super(nome, dinheiro);
        tipo = 2;
    }

    public void atualizarInformacoes(){
        try{
            String link = "https://coinmarketcap.com/currencies/" + this.nome;
        
        
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(10000) 
                    .get();
                    
            Element preco = doc.selectFirst("span[data-test=text-cdp-price-display]");
            
            if (preco != null) {
              String precoLimpo = preco.text().replace("$", "").replace(",", "").trim();
                this.preco_atual = Float.parseFloat(precoLimpo);
            } else {
                this.preco_atual = 0;
                System.out.println("Elemento de preço não encontrado no HTML retornado para: " + this.nome);
            }
            
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
