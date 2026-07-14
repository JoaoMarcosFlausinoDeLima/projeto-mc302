package projeto.sistemas;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import projeto.excecoes.InvalidAssetException;
import projeto.excecoes.PersistenceException;
import projeto.interfaces.Persistivel;
import projeto.investimentos.Açao;
import projeto.investimentos.Criptomoeda;
import projeto.investimentos.Fii;
import projeto.investimentos.FinancialAsset;
import projeto.investimentos.FundoDeInvestimento;
import projeto.persistencia.GsonFactory;


public class Carteira implements Persistivel {

    private static final String ARQUIVO = "carteira.json";

    private List<FinancialAsset> investimentos = new ArrayList<>();

    /**
     * Cadastra um novo ativo na carteira após validar os dados informados.
     *
     * @param tipo tipo do ativo ("Ação", "Fii", "FundoDeInvestimento", "TituloRendaFixa", "Criptomoeda")
     * @param nome nome ou código do ativo
     * @param dinheiro valor investido inicialmente
     * @throws InvalidAssetException se o nome for vazio, o valor não for positivo ou o tipo for desconhecido
     */
    public void cadastra(String tipo, String nome, int dinheiro) throws InvalidAssetException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new InvalidAssetException("Nome do ativo não pode ser vazio.");
        }
        if (dinheiro <= 0) {
            throw new InvalidAssetException("Valor investido deve ser maior que zero.");
        }
        switch (tipo) {
            case "Ação":
                investimentos.add(new Açao(nome, dinheiro));
                break;
            case "Fii":
                investimentos.add(new Fii(nome, dinheiro));
                break;
            
            case "Criptomoeda":
                investimentos.add(new Criptomoeda(nome, dinheiro));
                break;
            default:
                throw new InvalidAssetException("Tipo de ativo inválido: " + tipo);
        }
    }

    /**
     * Cadastra um título de renda fixa na carteira.
     *
     * @param nome nome do ativo
     * @param dinheiro valor investido
     * @param tipo tipo de título (pré-fixado, pós-fixado, misto)
     * @param indice índice de correção (por exemplo IPCA)
     * @param taxa taxa contratada do título
     * @param data_de_compra data de compra em vetor [dia, mês, ano]
     * @param data_de_vencimento data de vencimento em vetor [dia, mês, ano]
     * @param ultima_atu última atualização da cotação em vetor [dia, mês, ano]
     * @throws InvalidAssetException se os dados do título forem inválidos
     */
    public void cadastra_titulo(String nome, float dinheiro,String tipo,String indice,float taxa,int data_de_compra[],int data_de_vencimento[],int ultima_atu[]) throws InvalidAssetException{
        investimentos.add(new projeto.investimentos.TituloRendaFixa(nome, dinheiro, tipo, indice, taxa, data_de_compra,  data_de_vencimento, ultima_atu));
    }

    /**
     * Cadastra um fundo de investimento na carteira.
     *
     * @param nome nome do fundo
     * @param dinheiro valor investido
     * @param taxa_administracao taxa administrativa do fundo
     * @param taxa_performance taxa de performance do fundo
     * @param tipo categoria do fundo
     * @param benchmark índice de referência do fundo
     * @param cotacao valor atual da cota
     * @param quantidade_de_cotas número de cotas adquiridas
     * @throws InvalidAssetException se os dados do fundo forem inválidos
     */
    public void cadastra_fundo(String nome, float dinheiro,float taxa_administracao,float taxa_performance,String tipo,String benchmark,float cotacao,int quantidade_de_cotas) throws InvalidAssetException{
        investimentos.add(new projeto.investimentos.FundoDeInvestimento(nome, dinheiro, taxa_administracao, taxa_performance, tipo, benchmark, cotacao, quantidade_de_cotas));
    }


     /**
     * atualiza manualmente os novos valores do fundo de investimento
     *
     */
    public void editar_fundo(String nome, float cotacao, int quantidade) throws InvalidAssetException {
        for(FinancialAsset ativo: investimentos){
            if(ativo.getNome().equals(nome) && ativo instanceof FundoDeInvestimento){
                ((FundoDeInvestimento) ativo).editar(cotacao, (float) quantidade);
            }
        }
    }
   

    /**
     *
     * @param nome nome do ativo a comprar
     * @param dinheiro valor a ser investido
     * @throws InvalidAssetException se o valor não for positivo ou o ativo não estiver cadastrado
     */
    public void compra(String nome, int dinheiro) throws InvalidAssetException {
        if (dinheiro <= 0) {
            throw new InvalidAssetException("Valor de compra deve ser maior que zero.");
        }
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                ativo.comprar(dinheiro);
                return;
            }
        }
        throw new InvalidAssetException("Ativo não cadastrado: " + nome);
    }

  

    /** 
     *
     * @param nome nome do ativo a editar
     * @param novaQuantidade nova quantidade de unidades
     * @param novoInvestido novo valor investido
     * @throws InvalidAssetException se os valores não forem positivos ou o ativo não existir
     */
    public void editar(String nome, float novaQuantidade, float novoInvestido) throws InvalidAssetException {
        if (novaQuantidade <= 0) {
            throw new InvalidAssetException("Quantidade deve ser maior que zero.");
        }
        if (novoInvestido <= 0) {
            throw new InvalidAssetException("Valor investido deve ser maior que zero.");
        }
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                ativo.editar(novaQuantidade, novoInvestido);
                return;
            }
        }
        throw new InvalidAssetException("Ativo não cadastrado: " + nome);
    }

    /**
     * Remove um ativo da carteira pelo nome.
     *
     * @param nome nome do ativo a remover
     */
    public void remove(String nome) {
        for (int i = 0; i < investimentos.size(); i++) {
            if (investimentos.get(i).getNome().equals(nome)) {
                investimentos.remove(i);
                return;
            }
        }
    }

    /**
     * Exibe um resumo detalhado de um ativo específico da carteira.
     *
     * @param nome nome do ativo a ser resumido
     */
    public void resumo(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                ativo.resumo();
                return;
            }
        }
    }

    /**
     * Retorna a variação monetária de um ativo específico.
     *
     * @param nome nome do ativo
     * @return diferença entre o valor atual e o valor investido
     */
    public double variacao(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                return ativo.calcularVariaçãoMonetaria();
            }
        }
        return 0;
    }

    /**
     * Vende uma quantidade específica de um ativo pelo nome.
     * @param nome
     * @param quantidade
     * @return
     * @throws InvalidAssetException
    */
    public float vender(String nome, int quantidade) throws InvalidAssetException {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                return (float) ativo.vender(quantidade);
            }
        }
        return 0;
    }

    /**
     * Atualiza informações externas de todos os ativos da carteira.
     *
     * @throws InvalidAssetException se algum ativo não puder ser atualizado
     */
    public void atualizarInformacoes() throws InvalidAssetException {
        for (FinancialAsset ativo : investimentos) {
            ativo.atualizarInformacoes();
        }
    }

    /**
     * Atualiza o valor renderizado de cada ativo e retorna a soma do rendimento.
     *
     * @return soma dos resultados de render() dos ativos da carteira
     */
    public float render() {
        float total = 0;
        for (FinancialAsset ativo : investimentos) {
            total += ativo.render();
        }
        return total;
    }

    /**
     * Retorna o preço atual de um ativo pelo nome.
     *
     * @param nome nome do ativo
     * @return preço atual do ativo, ou 0 se não encontrado
     */
    public float getPrecoAtivo(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                return (float) ativo.getPrecoAtual();
            }
        }
        return 0;
    }

    /**
     * Retorna a lista de investimentos atualmente cadastrados.
     *
     * @return lista de ativos da carteira
     */
    public List<FinancialAsset> getInvestimentos() {
        return investimentos;
    }

    // ---------------------------------------------------------------
    // Cálculos de desempenho da carteira
    // ---------------------------------------------------------------

    /**
     * @return soma do valor investido em todos os ativos da carteira
     */
    public double calcularTotalInvestido() {
        double total = 0;
        for (FinancialAsset ativo : investimentos) {
            total += ativo.getInvestido();
        }
        return total;
    }

    /**
     * @return soma do valor de mercado atual de todos os ativos
     */
    public double calcularValorTotal() {
        double total = 0;
        for (FinancialAsset ativo : investimentos) {
            total += ativo.getValorAtual();
        }
        return total;
    }

    /**
     * @return variação monetária total da carteira (lucro ou prejuízo em reais)
     */
    public double calcularVariacaoTotal() {
        return calcularValorTotal() - calcularTotalInvestido();
    }

    /**
     * @return rentabilidade geral da carteira em porcentagem; 0 se nada foi investido
     */
    public double calcularRentabilidadeGeral() {
        double investido = calcularTotalInvestido();
        if (investido == 0) {
            return 0;
        }
        return (calcularVariacaoTotal() / investido) * 100;
    }

    /**
     * Calcula o desempenho geral da carteira (rentabilidade percentual total).
     *
     * @return desempenho geral em porcentagem
     */
    public double calcularDesempenhoGeral() {
        return calcularRentabilidadeGeral();
    }

    /**
     * Organiza os ativos da carteira agrupados por tipo de ativo.
     *
     * @return mapa do nome do tipo para a lista de ativos daquele tipo
     */
    public Map<String, List<FinancialAsset>> organizarPorTipo() {
        Map<String, List<FinancialAsset>> grupos = new LinkedHashMap<>();
        for (FinancialAsset ativo : investimentos) {
            grupos.computeIfAbsent(ativo.getTipoNome(), k -> new ArrayList<>()).add(ativo);
        }
        return grupos;
    }

    // ---------------------------------------------------------------
    // Persistência (interface Persistivel)
    // ---------------------------------------------------------------

    @Override
    public void salvar() throws PersistenceException {
        Gson gson = GsonFactory.criar();
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            gson.toJson(this, writer);
        } catch (Exception e) {
            throw new PersistenceException("Falha ao salvar carteira: " + e.getMessage(), e);
        }
    }

    @Override
    public void carregar() throws PersistenceException {
        Gson gson = GsonFactory.criar();
        try (Reader reader = new FileReader(ARQUIVO)) {
            Carteira carregada = gson.fromJson(reader, Carteira.class);
            if (carregada != null && carregada.investimentos != null) {
                this.investimentos = carregada.investimentos;
            }
        } catch (Exception e) {
            throw new PersistenceException("Falha ao carregar carteira: " + e.getMessage(), e);
        }
    }

    public void resumos(){
        for(projeto.investimentos.FinancialAsset ativo: investimentos){
            ativo.resumo();
            System.out.println();
        }
    }

}
