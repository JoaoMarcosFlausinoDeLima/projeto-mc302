package projeto.relatorios;

import java.io.FileWriter;
import java.time.LocalDate;

import projeto.excecoes.PersistenceException;
import projeto.sistemas.Carteira;

/**
 * Estrutura base para todos os relatórios da carteira.
 *
 * <p>Define a data de geração e o contrato comum: gerar o texto do relatório e
 * exportá-lo para um arquivo.</p>
 */
public abstract class Report {

    /** Data em que o relatório foi gerado (formato ISO: aaaa-mm-dd). */
    protected String dataGeracao;

    /** Carteira que serve de fonte de dados para o relatório. */
    protected Carteira carteira;

    protected Report(Carteira carteira) {
        this.carteira = carteira;
        this.dataGeracao = LocalDate.now().toString();
    }

    /**
     * Monta o conteúdo textual do relatório.
     *
     * @return texto formatado do relatório
     */
    public abstract String gerar();

    /**
     * Exporta o relatório gerado para um arquivo de texto.
     *
     * @param caminho caminho do arquivo de destino
     * @throws PersistenceException se ocorrer falha ao escrever o arquivo
     */
    public void exportar(String caminho) throws PersistenceException {
        try (FileWriter writer = new FileWriter(caminho)) {
            writer.write(gerar());
        } catch (Exception e) {
            throw new PersistenceException("Falha ao exportar relatório: " + e.getMessage(), e);
        }
    }

    public String getDataGeracao() {
        return dataGeracao;
    }
}
