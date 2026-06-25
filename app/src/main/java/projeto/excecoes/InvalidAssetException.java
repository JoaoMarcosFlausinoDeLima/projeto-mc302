package projeto.excecoes;

/**
 * Lançada quando se tenta cadastrar ou editar um ativo com dados inválidos.
 *
 * <p>Exemplos: preço ou quantidade negativos, valor investido menor ou igual a
 * zero, nome vazio ou tipo de ativo desconhecido.</p>
 */
public class InvalidAssetException extends Exception {

    public InvalidAssetException(String mensagem) {
        super(mensagem);
    }

    public InvalidAssetException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
