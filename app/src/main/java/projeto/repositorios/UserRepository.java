package projeto.repositorios;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import com.google.gson.Gson;

import projeto.excecoes.PersistenceException;
import projeto.persistencia.GsonFactory;
import projeto.usuario.Usuario;

/**
 * Responsável por salvar, carregar e consultar os dados de um usuário (e sua
 * carteira) em arquivos JSON.
 *
 * <p>Cada usuário é gravado em um arquivo próprio dentro do diretório
 * {@code data/}, no formato {@code usuario_<nome>.json}.</p>
 */
public class UserRepository {

    private static final String DIRETORIO = "data";

    private String caminhoArquivo(String nome) {
        return DIRETORIO + File.separator + "usuario_" + nome + ".json";
    }

    /**
     * Indica se já existe um arquivo salvo para o usuário informado.
     *
     * @param nome nome do usuário
     * @return {@code true} se o usuário já estiver cadastrado em disco
     */
    public boolean existeUsuario(String nome) {
        return new File(caminhoArquivo(nome)).exists();
    }

    /**
     * Salva o usuário (e a carteira contida nele) em arquivo JSON.
     *
     * @param usuario usuário a ser persistido
     * @throws PersistenceException se ocorrer falha ao salvar
     */
    public void salvarUsuario(Usuario usuario) throws PersistenceException {
        Gson gson = GsonFactory.criar();
        new File(DIRETORIO).mkdirs();
        try (FileWriter writer = new FileWriter(caminhoArquivo(usuario.getNome()))) {
            gson.toJson(usuario, writer);
        } catch (Exception e) {
            throw new PersistenceException("Falha ao salvar usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Carrega o usuário a partir do arquivo JSON correspondente.
     *
     * @param nome nome do usuário a carregar
     * @return o usuário carregado, ou {@code null} se não existir arquivo
     * @throws PersistenceException se ocorrer falha ao ler o arquivo
     */
    public Usuario carregarUsuario(String nome) throws PersistenceException {
        if (!existeUsuario(nome)) {
            return null;
        }
        Gson gson = GsonFactory.criar();
        try (Reader reader = new FileReader(caminhoArquivo(nome))) {
            return gson.fromJson(reader, Usuario.class);
        } catch (Exception e) {
            throw new PersistenceException("Falha ao carregar usuário: " + e.getMessage(), e);
        }
    }
}
