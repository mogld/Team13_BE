package dbdr.Excel;

import dbdr.domain.excel.controller.ExcelController;
import dbdr.domain.excel.dto.CareworkerFileUploadResponseDto;
import dbdr.domain.excel.service.ExcelDownloadService;
import dbdr.domain.excel.service.ExcelUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExcelControllerTest {

    @InjectMocks
    private ExcelController excelController;

    @Mock
    private ExcelDownloadService excelDownloadService;

    @Mock
    private ExcelUploadService excelUploadService;

    private MockMultipartFile validFile;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validFile = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2, 3});
        invalidFile = new MockMultipartFile("file", "test.xlsx", "text/plain", new byte[]{1, 2, 3});
    }

    @Test
    void testDownloadCareworkerTemplate() {
        when(excelDownloadService.generateCareworkerTemplate()).thenReturn(new byte[]{1, 2, 3});

        ResponseEntity<byte[]> response = excelController.downloadCareworkerTemplate();

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testUploadCareworkerData_Success() {
        CareworkerFileUploadResponseDto responseDto = new CareworkerFileUploadResponseDto("test.xlsx", null, null);
        when(excelUploadService.uploadCareworkerExcel(any())).thenReturn(responseDto);

        ResponseEntity<CareworkerFileUploadResponseDto> response = excelController.uploadCareworkerData(validFile);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().storeName()).isEqualTo("test.xlsx");
    }

}
