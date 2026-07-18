package projeto.investimentos;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import projeto.excecoes.*;

/**
 * Representa um FII (Fundo de Investimento Imobiliário) como um ativo financeiro.
 *
 * <p>Esta classe guarda informações específicas de FIIs, como nome do fundo,
 * segmento, gestão, dividendos e valor de mercado.</p>
 */
public class Fii extends FinancialAsset{
    /**
     * Valor do dividendo anual por cota, em reais.
     * Calculado a partir do dividend yield e do preço atual da cota.
     */
    protected float dividendo_anual;

    /**
     * Nome completo do fundo imobiliário.
     */
    protected String nomeFundo;

    /**
     * Segmento do fundo, por exemplo: "Multicategoria", "Logística", "Tijolo".
     */
    protected String segmento;

    /**
     * Receita do fundo ou indicador de receita informado na fonte.
     */
    protected Float receita;

    /**
     * Valor de mercado do FII em reais.
     */
    protected Double valorMercado;

    /**
     * Menor cotação da cota nos últimos 52 semanas.
     */
    protected Float min52;

    /**
     * Maior cotação da cota nos últimos 52 semanas.
     */
    protected Float max52;

    /**
     * Número de cotas do fundo ou volume médio de negociação.
     * Nota: não é a quantidade que o usuário possui, mas um indicador da base do fundo.
     */
    protected int volumeMedio;

    /**
     * Tipo de gestão do fundo, como "Ativa" ou "Passiva".
     */
    protected String gestao;
    
    /**
     * Cria um FII com o código do ativo e o valor investido inicial.
     *
     * @param nome código do FII (ticker)
     * @param dinheiro valor investido inicialmente
     */
    public Fii(String nome,float dinheiro) throws InvalidAssetException{
        super(nome, dinheiro);
        tipo = 1;
    }

    /**
     * Atualiza as informações do FII usando os dados do site Fundamentus.
     *
     * <p>Busca dados como cotação, segmento, nome do fundo, tipo de gestão,
     * dividend yield, receita, valor de mercado e cotações de 52 semanas.</p>
     */
    public void atualizarInformacoes() throws InvalidAssetException{
        try {
            String url = "https://www.fundamentus.com.br/detalhes.php?papel= ".replace(" ", this.nome);
            Document doc = Jsoup.connect(url).get();
            

            Elements labels = doc.select("td.label");
            if(labels.isEmpty()){
                throw new InvalidAssetException("FII inexistente no site Fundamentus");
            }

            for(Element labelCol : labels){
                String label = labelCol.text();
                
                Element dataCol = labelCol.nextElementSibling();
                
                if(dataCol != null){
                    String textoDado = dataCol.text();
                    if(label.contains("Cotação")){
                        this.preco_atual = Float.parseFloat(textoDado.replace(",", "."));
                        
                    }
                    if(label.contains("Segmento")){
                        this.segmento = textoDado;
                    }
                    if(label.contains("Nome")){
                        this.nomeFundo = textoDado;
                    }
                    if(label.contains("Gestão")){
                        this.gestao = textoDado;
                    }
                    if(label.contains("Div. Yield")){
                        this.dividendo_anual = Float.parseFloat(textoDado.replace(",", ".").replace("%", ""));
                        this.dividendo_anual = this.dividendo_anual * (float) this.preco_atual / 100f;
                    }
                    if(label.contains("Receita")){
                        this.receita =  Float.parseFloat(textoDado.replace(".", "").replace(",", "."));
                    }

                    if(label.toLowerCase().contains("Valor de mercado")){
                        this.valorMercado =  Double.parseDouble(textoDado.replace(".", "").replace(",", "."));
                    }
                    if(label.toLowerCase().contains("min 52 sem") || label.toLowerCase().contains("min 52 sem")){
                        this.min52 =  Float.parseFloat(textoDado.replace(",", "."));
                    }
                    if(label.toLowerCase().contains("max 52 sem") || label.toLowerCase().contains("max 52 sem")){
                        this.max52 =  Float.parseFloat(textoDado.replace(",", "."));
                    }
                    if(label.toLowerCase().contains("Nro. Cotas")){
                        // número de cotas do total do fundo, não é o mesmo que quantidade de cotas que o usuário tem.
                        this.volumeMedio = Integer.parseInt(textoDado.replace(".", "").replace(",", "."));
                    }
                }
            }

        } catch (Exception e) {
            throw new InvalidAssetException("FII inesistente no site fundamentos");
       
        }
    }

    /**
     * Retorna o nome completo do fundo imobiliário.
     *
     * @return nome do fundo
     */
    public String getNomeFundo(){
        return nomeFundo;
    }

    /**
     * Retorna o segmento do FII.
     *
     * @return segmento do fundo
     */
    public String getSegmento(){
        return segmento;
    }

    /**
     * Retorna o tipo de gestão do fundo.
     *
     * @return gestão do FII
     */
    public String getGestao(){
        return gestao;
    }

    /**
     * Retorna a receita indicada pelo site.
     *
     * @return receita do fundo
     */
    public Float getReceita(){
        return receita;
    }

    /**
     * Retorna o valor de mercado total do FII.
     *
     * @return valor de mercado em reais
     */
    public Double getValorMercado(){
        return valorMercado;
    }

    /**
     * Retorna a menor cotação da cota em 52 semanas.
     *
     * @return preço mínimo de 52 semanas
     */
    public Float getMin52(){
        return min52;
    }

    /**
     * Retorna a maior cotação da cota em 52 semanas.
     *
     * @return preço máximo de 52 semanas
     */
    public Float getMax52(){
        return max52;
    }

    /**
     * Retorna o número total de cotas do fundo ou volume médio de negociação.
     *
     * @return número de cotas do fundo
     */
    public int getVolumeMedio(){
        return volumeMedio;
    }

    /**
     * Retorna o dividendo anual por cota em reais.
     *
     * @return dividendo anual por quota
     */
    public Float getDividendoAnual(){
        return dividendo_anual;
    }

    @Override
    public String getTipoNome(){
        return "FII";
    }

    /**
     * Calcula o rendimento anual estimado para a posição de cotas atual.
     *
     * @param data filtro de data não usado nesta implementação
     * @return valor do dividendo anual total para as cotas detidas
     */
    public double render(){
        return dividendo_anual * quantidade;
    }

    public void resumo(){
        System.out.println("FII: " + nomeFundo);
        System.out.println("Quantidade de cotas: " + quantidade);
        System.out.println("Valor investido: R$ " + investido);
        System.out.println("Variação monetária: R$ " + calcularVariaçãoMonetaria());
        System.out.println("Segmento: " + segmento);
        System.out.println("Gestão: " + gestao);
        System.out.println("Preço atual: R$" + preco_atual);
        System.out.println("Dividendo anual por cota: R$" + dividendo_anual);
        System.out.println("Valor de mercado: R$" + valorMercado);
        System.out.println("Receita: R$" + receita);
        System.out.println("Min 52 semanas: R$" + min52);
        System.out.println("Max 52 semanas: R$" + max52);
        System.out.println("Volume médio (nro. cotas): " + volumeMedio);

    }
}
