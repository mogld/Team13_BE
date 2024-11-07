package dbdr.Excel;

import dbdr.domain.excel.dto.CareworkerFileUploadResponseDto;
import dbdr.domain.excel.service.ExcelUploadService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;


class ExcelUploadServiceTest {

    @InjectMocks
    private ExcelUploadService excelUploadService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockFile = createMockExcelFile();
    }

    @Test
    void testUploadCareworkerExcel_Success() {
        CareworkerFileUploadResponseDto response = excelUploadService.uploadCareworkerExcel(mockFile);
        assertThat(response.storeName()).isEqualTo("test.xlsx");
    }


    private MockMultipartFile createMockExcelFile() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.createSheet("Sheet1").createRow(0).createCell(0).setCellValue("Sample Data");
            workbook.write(out);
            return new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
        }
    }
}

