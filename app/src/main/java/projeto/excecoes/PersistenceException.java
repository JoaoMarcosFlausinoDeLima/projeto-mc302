package projeto.excecoes;

/**
 * Lançada quando ocorre uma falha ao salvar, carregar, atualizar ou remover
 * dados da carteira do usuário no arquivo JSON.
 */
public class PersistenceException extends Exception {

    public PersistenceException(String mensagem) {
        super(mensagem);
    }

    public PersistenceException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
