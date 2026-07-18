package projeto.excecoes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExceptionClassesTest {

    @Test
    void invalidAssetExceptionStoresMessageAndCause() {
        Exception cause = new RuntimeException("causa");
        InvalidAssetException ex = new InvalidAssetException("mensagem", cause);

        assertEquals("mensagem", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void persistenceExceptionStoresMessageAndCause() {
        Exception cause = new RuntimeException("causa");
        PersistenceException ex = new PersistenceException("mensagem", cause);

        assertEquals("mensagem", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
