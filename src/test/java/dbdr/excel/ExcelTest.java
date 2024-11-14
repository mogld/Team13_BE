package dbdr.excel;

import dbdr.domain.excel.service.ExcelUploadService;
import dbdr.domain.excel.dto.*;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.repository.RecipientRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcelTest {

    @InjectMocks
    private ExcelUploadService excelUploadService;

    @Mock
    private CareworkerRepository careworkerRepository;

    @Mock
    private GuardianRepository guardianRepository;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Institution institution;
    private Careworker careworker;
    private Guardian guardian;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // Institution 객체 생성 및 ID 설정
        institution = Institution.builder()
                .institutionNumber(100L)
                .institutionName("Test Institution")
                .build();
        setId(institution, 1L);
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution));

        // Careworker와 Guardian 생성 후 ID 설정
        careworker = Careworker.builder()
                .name("김요양")
                .institution(institution)
                .build();
        setId(careworker, 10L);
        when(careworkerRepository.findById(10L)).thenReturn(Optional.of(careworker));

        guardian = Guardian.builder()
                .name("이보호")
                .institution(institution)
                .build();
        setId(guardian, 20L);
        when(guardianRepository.findById(20L)).thenReturn(Optional.of(guardian));
    }

    @Test
    void testUploadCareworkerExcel_WithFailedEntries() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 첫 번째 행 - 정상 데이터
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("John Doe");
        row1.createCell(1).setCellValue("johndoe@example.com");
        row1.createCell(2).setCellValue("01012345678");
        row1.createCell(3).setCellValue("password");

        // 두 번째 행 - 중복된 전화번호
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Jane Doe");
        row2.createCell(1).setCellValue("janedoe@example.com");
        row2.createCell(2).setCellValue("01012345678"); // 중복 번호
        row2.createCell(3).setCellValue("password2");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        MockMultipartFile file = new MockMultipartFile("file", "careworker.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());

        // Mock 설정
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.encode("password2")).thenReturn("encodedPassword2");
        when(careworkerRepository.existsByPhone("01012345678")).thenReturn(false, true); // 첫 번째는 false, 두 번째는 true
        when(careworkerRepository.existsByEmail("johndoe@example.com")).thenReturn(false);
        when(careworkerRepository.existsByEmail("janedoe@example.com")).thenReturn(false);

        Careworker mockCareworker = Careworker.builder()
                .institution(institution)
                .name("John Doe")
                .email("johndoe@example.com")
                .phone("01012345678")
                .loginPassword("encodedPassword")
                .build();
        when(careworkerRepository.save(any(Careworker.class))).thenReturn(mockCareworker);

        CareworkerFileUploadResponse response = excelUploadService.uploadCareworkerExcel(file, institution.getId());

        // 검증 - 성공 항목과 실패 항목의 개수 확인
        assertEquals(1, response.uploadedCareworkers().size());
        assertEquals(1, response.failedCareworkers().size());

        ExcelCareworkerResponse uploadedCareworker = response.uploadedCareworkers().get(0);
        assertEquals("John Doe", uploadedCareworker.getName());
        ExcelCareworkerResponse failedCareworker = response.failedCareworkers().get(0);
        assertEquals("Jane Doe", failedCareworker.getName());
    }

    @Test
    void testUploadGuardianExcel_WithFailedEntries() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 첫 번째 행 - 정상 데이터
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Jane Guardian");
        row1.createCell(1).setCellValue("01098765432");
        row1.createCell(2).setCellValue("password");

        // 두 번째 행 - 중복된 전화번호
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Jack Guardian");
        row2.createCell(1).setCellValue("01098765432"); // 중복 번호
        row2.createCell(2).setCellValue("password2");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        MockMultipartFile file = new MockMultipartFile("file", "guardian.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());

        // Mock 설정
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.encode("password2")).thenReturn("encodedPassword2");
        when(guardianRepository.existsByPhone("01098765432")).thenReturn(false, true);

        Guardian mockGuardian = Guardian.builder()
                .institution(institution)
                .name("Jane Guardian")
                .phone("01098765432")
                .loginPassword("encodedPassword")
                .build();
        when(guardianRepository.save(any(Guardian.class))).thenReturn(mockGuardian);

        GuardianFileUploadResponse response = excelUploadService.uploadGuardianExcel(file, institution.getId());

        // 검증 - 성공 항목과 실패 항목의 개수 확인
        assertEquals(1, response.uploadedGuardians().size());
        assertEquals(1, response.failedGuardians().size());

        ExcelGuardianResponse uploadedGuardian = response.uploadedGuardians().get(0);
        assertEquals("Jane Guardian", uploadedGuardian.getName());
        ExcelGuardianResponse failedGuardian = response.failedGuardians().get(0);
        assertEquals("Jack Guardian", failedGuardian.getName());
    }

    @Test
    void testUploadRecipientExcel_WithFailedEntries() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        // 첫 번째 행 - 정상 데이터
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Valid Recipient");
        row1.createCell(1).setCellValue("2000-01-01");
        row1.createCell(2).setCellValue("M");
        row1.createCell(3).setCellValue("3");
        row1.createCell(4).setCellValue("R12345");
        row1.createCell(5).setCellValue("2022-01-01");
        row1.createCell(6).setCellValue("10"); // Careworker ID
        row1.createCell(7).setCellValue("20"); // Guardian ID

        // 두 번째 행 - 중복된 care number
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Duplicate Recipient");
        row2.createCell(1).setCellValue("2000-01-01");
        row2.createCell(2).setCellValue("M");
        row2.createCell(3).setCellValue("3");
        row2.createCell(4).setCellValue("R12345"); // 중복 care number
        row2.createCell(5).setCellValue("2022-01-01");
        row2.createCell(6).setCellValue("10"); // Careworker ID
        row2.createCell(7).setCellValue("20"); // Guardian ID
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        MockMultipartFile file = new MockMultipartFile("file", "recipient.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());

        // 중복 확인에 대한 Mock 설정
        when(recipientRepository.existsByCareNumber("R12345")).thenReturn(false, true);


        RecipientFileUploadResponse response = excelUploadService.uploadRecipientExcel(file, institution.getId());

        // 검증 - 성공 항목과 실패 항목의 개수 확인
        assertEquals(1, response.uploadedRecipients().size());
        assertEquals(1, response.failedRecipients().size());

        // 성공적으로 업로드된 항목 검증
        ExcelRecipientResponse successRecipient = response.uploadedRecipients().get(0);
        assertEquals("Valid Recipient", successRecipient.getName());
        assertEquals("R12345", successRecipient.getCareNumber());

        // 실패한 항목 검증
        ExcelRecipientResponse failedRecipient = response.failedRecipients().get(0);
        assertEquals("Duplicate Recipient", failedRecipient.getName());
        assertEquals("R12345", failedRecipient.getCareNumber());
    }

    // 리플렉션을 사용하여 엔티티의 ID 값을 설정
    private void setId(Object entity, Long idValue) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, idValue);
    }
}