package dbdr.Excel;

import dbdr.domain.excel.service.ExcelDownloadService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExcelDownloadServiceTest {

    private final ExcelDownloadService excelDownloadService = new ExcelDownloadService();

    @Test
    void testGenerateCareworkerTemplate_Success() {
        byte[] result = excelDownloadService.generateCareworkerTemplate();
        assertThat(result).isNotEmpty();
    }

    @Test
    void testGenerateGuardianTemplate_Success() {
        byte[] result = excelDownloadService.generateGuardianTemplate();
        assertThat(result).isNotEmpty();
    }

    @Test
    void testGenerateRecipientTemplate_Success() {
        byte[] result = excelDownloadService.generateRecipientTemplate();
        assertThat(result).isNotEmpty();
    }

}

