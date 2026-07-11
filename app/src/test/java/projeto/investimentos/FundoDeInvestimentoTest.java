package projeto.investimentos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import projeto.excecoes.InvalidAssetException;

class FundoDeInvestimentoTest {

    @Test
    void constructorSetsTypeAndValorAtual()  throws InvalidAssetException{
        FundoDeInvestimento fundo = new FundoDeInvestimento("Multimercado", 200f,
                0.01f, 0.02f, "Multimercado", "IBOV", 15f, 5);

        assertEquals("Fundo de Investimento", fundo.getTipoNome());
        assertEquals(75f, fundo.getValorAtual(), 0.0001);
    }

    @Test
    void editarFundoUpdatesCotacaoAndQuantidade()  throws InvalidAssetException{
        FundoDeInvestimento fundo = new FundoDeInvestimento("Multimercado", 200f,
                0.01f, 0.02f, "Multimercado", "IBOV", 15, 5);

        fundo.editar_fundo(20f, 10f);

        assertEquals(200f, fundo.getValorAtual(), 0.0001);
        assertEquals(10f, fundo.getQuantidade(), 0.0001);
    }
}
