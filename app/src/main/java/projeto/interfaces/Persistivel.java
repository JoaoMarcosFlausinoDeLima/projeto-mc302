package projeto.interfaces;

import projeto.excecoes.PersistenceException;

/**
 * Contrato para objetos capazes de salvar e carregar seu estado em arquivo.
 */
public interface Persistivel {

    /**
     * Grava o estado atual do objeto em arquivo.
     *
     * @throws PersistenceException se ocorrer falha ao salvar
     */
    void salvar() throws PersistenceException;

    /**
     * Recupera o estado do objeto a partir de um arquivo.
     *
     * @throws PersistenceException se ocorrer falha ao carregar
     */
    void carregar() throws PersistenceException;
}
