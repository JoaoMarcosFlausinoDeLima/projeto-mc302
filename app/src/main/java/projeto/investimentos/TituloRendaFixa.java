package projeto.investimentos;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class TituloRendaFixa extends FinancialAsset {
    protected float dividendo;
    private String tipo; // pos fixado/misto, pre fixado
    private float taxa; // selic IPCA
    private String indice; // IPCA
    private LocalDate data_de_compra; // data que o fundo foi comprado
    private LocalDate data_de_vencimento; // data de vencimento do fundo
    private LocalDate ultima_atu; // ultima vez que o valor foi atualizado
    
    

    public TituloRendaFixa(String nome, float dinheiro,String tipo,String indice,float taxa,int data_de_compra[],int data_de_vencimento[],int ultima_atu[]){
        super(nome, dinheiro);
        this.dinheiro_total = dinheiro;
        this.tipo = tipo; // pos ou pre
        this.taxa = taxa;
        this.indice = indice;
        this.data_de_compra = LocalDate.of(data_de_compra[2],data_de_compra[1],data_de_compra[0]);
        this.data_de_vencimento = LocalDate.of(data_de_vencimento[2],data_de_vencimento[1],data_de_vencimento[0]);
        this.ultima_atu = LocalDate.of(ultima_atu[2],ultima_atu[1],ultima_atu[0]);
        


    }

    @Override
    public void comprar(float dinheiro){
        investido += dinheiro;
        dinheiro_total += dinheiro;   
        
    }

    public float buscarIPCA(int mes,int ano){
        try {
            YearMonth anoMes = YearMonth.of(ano, mes);
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
            String dataInicial = anoMes.atDay(1).format(formatador);
            String dataFinal = anoMes.atEndOfMonth().format(formatador);
            
            String url = String.format("https://api.bcb.gov.br/dados/serie/bcdata.sgs.%d/dados?formato=json&dataInicial=%s&dataFinal=%s",
                    433, dataInicial, dataFinal
                );
            
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> resposta = cliente.send(req,HttpResponse.BodyHandlers.ofString());
            
            String json = resposta.body();
            
            String valor = json.split("\"")[7];
            
            return Float.parseFloat(valor)/100;
        } catch (Exception e) {
            System.err.println("Erro ao buscar os dados: " + e.getMessage());
            return 0;
        }
        
    }

    public float buscarSELIC(int mes,int ano){
        try {
            YearMonth anoMes = YearMonth.of(ano, mes);
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
            String dataInicial = anoMes.atDay(1).format(formatador);
            String dataFinal = anoMes.atEndOfMonth().format(formatador);
            
            String url = String.format("https://api.bcb.gov.br/dados/serie/bcdata.sgs.%d/dados?formato=json&dataInicial=%s&dataFinal=%s",
                    4390, dataInicial, dataFinal
                );
            
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> resposta = cliente.send(req,HttpResponse.BodyHandlers.ofString());
            
            String json = resposta.body();
         
            if(json.equals("[]")){
                return 0;
            }

            String valor = json.split("\"")[7];
            return Float.parseFloat(valor)/100;

        } catch (Exception e) {
            System.err.println("Erro ao buscar os dados: " + e.getMessage());
            return 0;
        }
        
    }

    public void atualizarInformacoes(){
        // ATUALIZA O PREÇO DO TITULO
        try{
            LocalDate dataAtual = LocalDate.now();
            if(ultima_atu.isBefore(data_de_vencimento)){
                while(ultima_atu.isBefore(dataAtual)){
                    if(tipo.equals("pos")){
                        if(indice.equals("SELIC")){
                            this.dinheiro_total = dinheiro_total*(1+this.taxa+buscarSELIC(ultima_atu.getMonthValue(),ultima_atu.getYear()));
                        }
                        if(indice.equals("IPCA")){
                            this.dinheiro_total = dinheiro_total*(1+this.taxa+buscarIPCA(ultima_atu.getMonthValue(),ultima_atu.getYear()));
                        }
                        
                    }else if(tipo.equals("pre")){
                        this.preco_atual = preco_atual*(1+this.taxa);
                    }
                    ultima_atu = ultima_atu.plusMonths(1);
                }
        }

        } catch (Exception e){
            System.out.println("Erro ao atu");
        }
    }

   
    
   

    public double render(){

            return 0;
    }

    @Override
    public String getTipoNome(){
        return "Título de Renda Fixa";
    }

    public void resumo(){
        
        System.out.println("Título de Renda Fixa: " + this.nome);
        System.out.println("Tipo: " + this.tipo);
        System.out.println("Índice: " + this.indice);
        System.out.println("Taxa: " + this.taxa);
        System.out.println("Data de compra: " + this.data_de_compra);
        System.out.println("Data de vencimento: " + this.data_de_vencimento);
        System.out.println("Última atualização: " + this.ultima_atu);
        System.out.println("Valor investido: R$ " + this.investido);
        System.out.println("Valor atual: R$ " + this.dinheiro_total);
        System.out.println("Variação monetária: R$ " + calcularVariaçãoMonetaria());
    }
}
