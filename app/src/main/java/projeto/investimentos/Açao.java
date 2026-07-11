package projeto.investimentos;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import projeto.excecoes.InvalidAssetException;

/**
 * Representa uma ação de bolsa como um ativo financeiro.
 *
 * <p>Esta classe guarda métricas próprias de ações, como dividendos, P/L, P/VP,
 * valor de mercado e cotações de 52 semanas.</p>
 */
public class Açao extends FinancialAsset{
    /**
     * Valor do dividendo anual por ação, em reais.
     * Calculado a partir do dividend yield e do preço atual.
     */
    protected float dividendo_anual;

    /**
     * Nome da empresa emissora da ação.
     */
    protected String empresa;

    /**
     * Relação preço/lucro (P/L) da ação.
     * Indica quantas vezes o mercado está pagando o lucro anual da empresa.
     */
    protected Float pl;

    /**
     * Relação preço/valor patrimonial (P/VPA).
     * Indica quantas vezes a ação está sendo negociada em relação ao valor patrimonial por ação.
     */
    protected Float pvp;

    /**
     * Valor patrimonial por ação.
     */
    protected Float vpa;

    /**
     * Valor de mercado da empresa, em reais.
     */
    protected Double valorMercado;

    /**
     * Menor preço da ação nos últimos 52 semanas.
     */
    protected Float min52;

    /**
     * Maior preço da ação nos últimos 52 semanas.
     */
    protected Float max52;

  
    /**
     * Cria uma ação com o código do ativo e o valor investido inicial.
     *
     * @param nome código da ação (ticker)
     * @param dinheiro valor investido inicialmente
     */
    public Açao(String nome,float dinheiro) throws InvalidAssetException{
        super(nome, dinheiro);
        
        tipo = 0;
        
    }

    /**
     * Atualiza as informações da ação usando os dados do site Fundamentus.
     *
     * <p>Busca dados como cotação, empresa, dividend yield, P/L, valor de mercado,
     * mínimas e máximas de 52 semanas e número de ações.</p>
     */
    public void atualizarInformacoes() throws InvalidAssetException{
        try {
            String url = "https://www.fundamentus.com.br/detalhes.php?papel= ".replace(" ", this.nome);
            Document doc = Jsoup.connect(url).get();
            
            Elements labels = doc.select("td.label");

            if(labels.isEmpty()){
                throw new InvalidAssetException("Ação inexistente no site Fundamentus");
            }

            for(Element labelCol : labels){
                
                String label = labelCol.text();
                
                Element dataCol = labelCol.nextElementSibling();
                
                if(dataCol != null){
                    String textoDado = dataCol.text();
                    if(label.contains("Cotação")){
                        
                        this.preco_atual = Float.parseFloat(textoDado.replace(".","").replace(",", "."));
                    }
                    if(label.contains("Empresa")){
                        this.empresa = textoDado;
                    }
                    if(label.contains("Div. Yield")){
                        this.dividendo_anual = Float.parseFloat(textoDado.replace(",", ".").replace("%", ""));
                        this.dividendo_anual = this.dividendo_anual * (float) this.preco_atual / 100f;
                    }
                    if(label.contains("P/L") || label.equalsIgnoreCase("P/L")){
                        this.pl =  Float.parseFloat(textoDado.replace(",", ".").replace("%", ""));
                    }

                    if(label.toLowerCase().contains("Valor de mercado")){
                         this.valorMercado =  Double.parseDouble(textoDado.replace(".", "").replace(",", "."));
                    }
                    if(label.toLowerCase().contains("min 52") || label.toLowerCase().contains("min 52 sem")){
                        this.min52 =  Float.parseFloat(textoDado.replace(",", "."));
                    }
                    if(label.toLowerCase().contains("max 52") || label.toLowerCase().contains("max 52 sem")){
                        this.max52 =  Float.parseFloat(textoDado.replace(",", "."));
                    }
                    
                }
            }

        } catch (Exception e) {
            throw new InvalidAssetException("Ação inesistente no site fundamentos");
        
        }
    }

    /**
     * Retorna o nome da empresa emissora da ação.
     *
     * @return nome da empresa
     */
    public String getEmpresa(){
        return empresa;
    }

    /**
     * Retorna o índice P/L da ação.
     *
     * @return preço sobre lucro
     */
    public Float getPL(){
        return pl;
    }

    /**
     * Retorna o índice P/VPA da ação.
     *
     * @return preço sobre valor patrimonial
     */
    public Float getPVP(){
        return pvp;
    }

    /**
     * Retorna o valor patrimonial por ação.
     *
     * @return VPA da ação
     */
    public Float getVPA(){
        return vpa;
    }

    /**
     * Retorna o valor de mercado da empresa emissora.
     *
     * @return market cap em reais
     */
    public Double getValorMercado(){
        return valorMercado;
    }

    /**
     * Retorna a menor cotação dos últimos 52 semanas.
     *
     * @return preço mínimo de 52 semanas
     */
    public Float getMin52(){
        return min52;
    }

    /**
     * Retorna a maior cotação dos últimos 52 semanas.
     *
     * @return preço máximo de 52 semanas
     */
    public Float getMax52(){
        return max52;
    }

    public Float getDividendoAnual(){
        return dividendo_anual;
    }

    @Override
    public String getTipoNome(){
        return "Ação";
    }


    /**
     * Calcula o rendimento anual estimado em reais para a quantidade de ações detida.
     *
     * @param data filtro de data não usado nesta implementação
     * @return valor do dividendo anual total para a posição atual
     */
    public double render(){
        return this.dividendo_anual*this.quantidade;
        
    }

    public void resumo(){
        System.out.println("Ação: " + this.nome);
        System.out.println("Empresa: " + this.empresa);
        System.out.println("Preço atual: R$ " + this.preco_atual);
        System.out.println("Quantidade de cotas: " + this.quantidade);
        System.out.println("Valor investido: R$ " + this.investido);
        System.out.println("P/L: " + this.pl);
        System.out.println("Valor de mercado: R$ " + this.valorMercado);
        System.out.println("Mínimo 52 semanas: R$ " + this.min52);
        System.out.println("Máximo 52 semanas: R$ " + this.max52);
        System.out.println("Dividendo anual por ação: R$ " + this.dividendo_anual);
        System.out.println("Variação monetária: R$ " + calcularVariaçãoMonetaria());
    }
}
