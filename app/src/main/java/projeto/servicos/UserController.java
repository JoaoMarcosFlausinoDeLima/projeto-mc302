package projeto.servicos;

import projeto.excecoes.PersistenceException;
import projeto.repositorios.UserRepository;
import projeto.usuario.Usuario;

/**
 * Controla as ações do usuário no sistema: cadastro, login e acesso ao usuário
 * atualmente logado.
 *
 * <p>Delega a persistência ao {@link UserRepository}. A navegação entre telas
 * será conectada quando a interface gráfica (JavaFX) for implementada.</p>
 */
public class UserController {

    private final UserRepository repositorio;
    private Usuario usuarioAtual;

    public UserController() {
        this.repositorio = new UserRepository();
    }

    /**
     * Cadastra um novo usuário e o persiste em disco.
     *
     * @param nome nome do usuário
     * @param senha senha do usuário
     * @return o usuário criado
     * @throws PersistenceException se já existir um usuário com esse nome ou
     *         se houver falha ao salvar
     */
    public Usuario cadastrarUsuario(String nome, String senha) throws PersistenceException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new PersistenceException("Nome de usuário não pode ser vazio.");
        }
        if (repositorio.existeUsuario(nome)) {
            throw new PersistenceException("Já existe um usuário com o nome: " + nome);
        }
        Usuario novo = new Usuario(nome, senha);
        repositorio.salvarUsuario(novo);
        this.usuarioAtual = novo;
        return novo;
    }

    /**
     * Realiza o login de um usuário existente.
     *
     * @param nome nome do usuário
     * @param senha senha informada
     * @return {@code true} se o login for bem-sucedido
     * @throws PersistenceException se houver falha ao carregar o usuário
     */
    public boolean realizarLogin(String nome, String senha) throws PersistenceException {
        Usuario usuario = repositorio.carregarUsuario(nome);
        if (usuario != null && usuario.autenticar(senha)) {
            this.usuarioAtual = usuario;
            return true;
        }
        return false;
    }

    /**
     * Salva o estado atual do usuário logado (incluindo sua carteira).
     *
     * @throws PersistenceException se não houver usuário logado ou falhar ao salvar
     */
    public void salvarUsuarioAtual() throws PersistenceException {
        if (usuarioAtual == null) {
            throw new PersistenceException("Nenhum usuário logado para salvar.");
        }
        usuarioAtual.registrarSnapshotMensal(
                usuarioAtual.getCarteira().calcularTotalInvestido(),
                usuarioAtual.getCarteira().calcularValorTotal());
        repositorio.salvarUsuario(usuarioAtual);
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }
}
