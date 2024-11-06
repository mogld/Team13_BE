package dbdr.domain.excel.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.excel.dto.*;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final CareworkerRepository careworkerRepository;
    private final GuardianRepository guardianRepository;
    private final RecipientRepository recipientRepository;
    private final InstitutionRepository institutionRepository;

    @Transactional
    public CareworkerFileUploadResponseDto uploadCareworkerExcel(MultipartFile file) {
        Set<String> seenPhones = new HashSet<>();
        List<ExcelCareworkerResponseDto> uploaded = new ArrayList<>();
        List<ExcelCareworkerResponseDto> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processCareworkerRow(row, uploaded, failed, seenPhones));

        return new CareworkerFileUploadResponseDto(file.getOriginalFilename(), uploaded, failed);
    }

    @Transactional
    public GuardianFileUploadResponseDto uploadGuardianExcel(MultipartFile file) {
        Set<String> seenPhones = new HashSet<>();
        List<ExcelGuardianResponseDto> uploaded = new ArrayList<>();
        List<ExcelGuardianResponseDto> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processGuardianRow(row, uploaded, failed, seenPhones));

        return new GuardianFileUploadResponseDto(file.getOriginalFilename(), uploaded, failed);
    }

    @Transactional
    public RecipientFileUploadResponseDto uploadRecipientExcel(MultipartFile file) {
        Set<String> seenCareNumbers = new HashSet<>();
        List<ExcelRecipientResponseDto> uploaded = new ArrayList<>();
        List<ExcelRecipientResponseDto> failed = new ArrayList<>();

        processExcelFile(file, (row) -> processRecipientRow(row, uploaded, failed, seenCareNumbers));

        return new RecipientFileUploadResponseDto(file.getOriginalFilename(), uploaded, failed);
    }

    private void processExcelFile(MultipartFile file, RowProcessor rowProcessor) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                rowProcessor.process(row);
            }
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_UPLOAD_ERROR);
        }
    }

    private void processCareworkerRow(Row row, List<ExcelCareworkerResponseDto> successList,
                                      List<ExcelCareworkerResponseDto> failedList, Set<String> seenPhones) {
        Long institutionId = Long.valueOf(getCellValue(row.getCell(0)));
        String name = getCellValue(row.getCell(1));
        String email = getCellValue(row.getCell(2));
        String phone = getCellValue(row.getCell(3));

        //ID로 조회
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));
        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);
            validatePhone(phone, careworkerRepository.existsByPhone(phone));
            seenPhones.add(phone);

            Careworker careworker = Careworker.builder()
                    .institution(institution)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .build();
            careworkerRepository.save(careworker);

            successList.add(new ExcelCareworkerResponseDto(careworker.getId(), institutionId, name, email, phone));
        } catch (ApplicationException e) {
            failedList.add(new ExcelCareworkerResponseDto(null, institutionId, name, email, phone));
        }
    }

    private void processGuardianRow(Row row, List<ExcelGuardianResponseDto> successList,
                                    List<ExcelGuardianResponseDto> failedList, Set<String> seenPhones) {
        String name = getCellValue(row.getCell(0));
        String phone = getCellValue(row.getCell(1));

        try {
            checkDuplicate(seenPhones, phone, ApplicationError.DUPLICATE_PHONE);
            validatePhone(phone, guardianRepository.existsByPhone(phone));
            seenPhones.add(phone);

            Guardian guardian = Guardian.builder()
                    .name(name)
                    .phone(phone)
                    .build();
            guardianRepository.save(guardian);

            successList.add(new ExcelGuardianResponseDto(name, phone));
        } catch (ApplicationException e) {
            failedList.add(new ExcelGuardianResponseDto(name, phone));
        }
    }

    private void processRecipientRow(Row row, List<ExcelRecipientResponseDto> successList,
                                     List<ExcelRecipientResponseDto> failedList, Set<String> seenCareNumbers) {
        String name = getCellValue(row.getCell(0));
        String birth = getCellValue(row.getCell(1));
        String gender = getCellValue(row.getCell(2));
        String careLevel = getCellValue(row.getCell(3));
        String careNumber = getCellValue(row.getCell(4));
        String startDate = getCellValue(row.getCell(5));
        Long institutionId = Long.valueOf(getCellValue(row.getCell(6)));

        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND));

        try {
            checkDuplicate(seenCareNumbers, careNumber, ApplicationError.DUPLICATE_CARE_NUMBER);
            validateCareNumber(careNumber, recipientRepository.existsByCareNumber(careNumber));
            seenCareNumbers.add(careNumber);

            Recipient recipient = Recipient.builder()
                    .name(name)
                    .careNumber(careNumber)
                    .birth(LocalDate.parse(birth))
                    .gender(gender)
                    .careLevel(careLevel)
                    .startDate(LocalDate.parse(startDate))
                    .institution(institution)
                    .build();
            recipientRepository.save(recipient);

            successList.add(new ExcelRecipientResponseDto(
                    recipient.getId(), name, LocalDate.parse(birth), gender, careLevel, careNumber, LocalDate.parse(startDate), institution.getInstitutionName()));
        } catch (ApplicationException e) {
            failedList.add(new ExcelRecipientResponseDto(
                    null, name, LocalDate.parse(birth), gender, careLevel, careNumber, LocalDate.parse(startDate), institution.getInstitutionName()));
        }
    }

    private void checkDuplicate(Set<String> seenSet, String value, ApplicationError error) {
        if (seenSet.contains(value)) {
            throw new ApplicationException(error);
        }
    }

    private void validatePhone(String phone, boolean exists) {
        if (!phone.matches("010\\d{8}")) {
            throw new ApplicationException(ApplicationError.INVALID_PHONE_NUMBER);
        }
        if (exists) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    private void validateCareNumber(String careNumber, boolean exists) {
        if (exists) {
            throw new ApplicationException(ApplicationError.DUPLICATE_CARE_NUMBER);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().toString();
                } else {
                    return String.format("%.0f", cell.getNumericCellValue()).trim();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            default:
                return "";
        }
    }

    @FunctionalInterface
    private interface RowProcessor {
        void process(Row row);
    }
}
