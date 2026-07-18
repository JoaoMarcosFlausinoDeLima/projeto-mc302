package projeto.servicos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import projeto.relatorios.Report;
import projeto.sistemas.Carteira;

class ReportServiceTest {

    @Test
    void gerarRelatorioGeralReturnsReportGeral() {
        ReportService service = new ReportService();
        Report report = service.gerarRelatorioGeral(new Carteira());

        assertEquals("projeto.relatorios.RelatorioGeral", report.getClass().getName());
    }

    @Test
    void gerarRelatorioPorTipoReturnsReportPorTipo() {
        ReportService service = new ReportService();
        Report report = service.gerarRelatorioPorTipo(new Carteira());

        assertEquals("projeto.relatorios.RelatorioPorTipo", report.getClass().getName());
    }
}
