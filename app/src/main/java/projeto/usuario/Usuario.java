package projeto.usuario;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import projeto.sistemas.Carteira;

/**
 * Representa um usuário do sistema, dono de uma carteira de investimentos.
 */
public class Usuario {

    private String nome;
    private String senha;
    private Carteira carteira;

    /**
     * Histórico mensal da carteira (chave "aaaa-mm"), guardando
     * [valor investido, valor de mercado atual] de cada mês.
     */
    private Map<String, double[]> historicoMensal = new LinkedHashMap<>();

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.carteira = new Carteira();
    }

    public String getNome() {
        return nome;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    /**
     * Registra o snapshot do mês atual (sobrescreve o registro do mês, se já
     * existir).
     *
     * @param investido valor total investido na carteira
     * @param valorAtual valor de mercado total da carteira
     */
    public void registrarSnapshotMensal(double investido, double valorAtual) {
        registrarSnapshotMensal(YearMonth.now(), investido, valorAtual);
    }

    /**
     * Registra o snapshot em um mês específico (sobrescreve, se já existir).
     *
     * @param mes mês de referência
     * @param investido valor total investido na carteira
     * @param valorAtual valor de mercado total da carteira
     */
    public void registrarSnapshotMensal(YearMonth mes, double investido, double valorAtual) {
        getHistoricoMensal().put(mes.toString(), new double[]{investido, valorAtual});
    }

    public Map<String, double[]> getHistoricoMensal() {
        if (historicoMensal == null) {
            historicoMensal = new LinkedHashMap<>();
        }
        return historicoMensal;
    }

    /**
     * Verifica se a senha informada confere com a do usuário.
     *
     * @param senha senha a ser validada
     * @return {@code true} se a senha estiver correta
     */
    public boolean autenticar(String senha) {
        return this.senha != null && this.senha.equals(senha);
    }
}
