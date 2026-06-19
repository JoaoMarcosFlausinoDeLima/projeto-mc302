package projeto.investimentos;



public class TituloRendaFixa extends FinancialAsset {
    protected float dividendo;

    public TituloRendaFixa(String nome, float dinheiro){
        super(nome, dinheiro);
    }

    public void atualizarInformacoes(){
        try{
            //Stock stick = YahooFinance.get(this.nome);
            //this.preco_atual = stick.getQuote().getPrice().floatValue();

        } catch (Exception e){
            System.out.println("Erro ao atu");
        }
    }

    public float getDividendo(){
        return dividendo;
    }
    
   

    public double render(){
        this.preco_atual = preco_atual*(1+this.dividendo);
        return 0;
    }
    public void resumo(){}
}
