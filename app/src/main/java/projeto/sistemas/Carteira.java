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
import projeto.persistencia.GsonFactory;

/**
 * Núcleo do sistema: armazena e gerencia os ativos financeiros da carteira.
 *
 * <p>Implementa {@link Persistivel} para salvar e carregar a carteira em JSON.</p>
 */
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
            case "FundoDeInvestimento":
                investimentos.add(new projeto.investimentos.FundoDeInvestimento(nome, dinheiro));
                break;
            case "TituloRendaFixa":
                investimentos.add(new projeto.investimentos.TituloRendaFixa(nome, dinheiro));
                break;
            case "Criptomoeda":
                investimentos.add(new Criptomoeda(nome, dinheiro));
                break;
            default:
                throw new InvalidAssetException("Tipo de ativo inválido: " + tipo);
        }
    }

    /**
     * Compra mais unidades de um ativo já cadastrado na carteira.
     *
     * @param nome nome do ativo a comprar
     * @param dinheiro valor a ser investido
     * @throws InvalidAssetException se o valor não for positivo ou o ativo não estiver cadastrado
     */
    public void compra(String nome, float dinheiro) throws InvalidAssetException {
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
     * Edita um ativo já cadastrado, redefinindo quantidade e valor investido.
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

    public void remove(String nome) {
        for (int i = 0; i < investimentos.size(); i++) {
            if (investimentos.get(i).getNome().equals(nome)) {
                investimentos.remove(i);
                return;
            }
        }
    }

    public void resumo(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                ativo.resumo();
                return;
            }
        }
    }

    public double variacao(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                return ativo.calcularVariaçãoMonetaria();
            }
        }
        return 0;
    }

    /**
     * Vende unidades de um ativo já cadastrado na carteira.
     *
     * @param nome nome do ativo a vender
     * @param quantidade quantidade de unidades a vender
     * @return valor recebido pela venda
     * @throws InvalidAssetException se a quantidade não for positiva, exceder a
     *         posse atual ou o ativo não estiver cadastrado
     */
    public float vender(String nome, float quantidade) throws InvalidAssetException {
        if (quantidade <= 0) {
            throw new InvalidAssetException("Quantidade a vender deve ser maior que zero.");
        }
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                if (quantidade > ativo.getQuantidade()) {
                    throw new InvalidAssetException(String.format(
                            "Quantidade insuficiente: você possui %.2f unidades.", ativo.getQuantidade()));
                }
                return (float) ativo.vender(quantidade);
            }
        }
        throw new InvalidAssetException("Ativo não cadastrado: " + nome);
    }

    public void atualizarInformacoes() {
        for (FinancialAsset ativo : investimentos) {
            ativo.atualizarInformacoes();
        }
    }

    public float render() {
        float total = 0;
        for (FinancialAsset ativo : investimentos) {
            total += ativo.render();
        }
        return total;
    }

    public float getPrecoAtivo(String nome) {
        for (FinancialAsset ativo : investimentos) {
            if (ativo.getNome().equals(nome)) {
                return (float) ativo.getPrecoAtual();
            }
        }
        return 0;
    }

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
}
