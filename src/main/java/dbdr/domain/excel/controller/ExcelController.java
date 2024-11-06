package dbdr.domain.excel.controller;

import dbdr.domain.excel.dto.CareworkerFileUploadResponseDto;
import dbdr.domain.excel.dto.GuardianFileUploadResponseDto;
import dbdr.domain.excel.dto.RecipientFileUploadResponseDto;
import dbdr.domain.excel.service.ExcelDownloadService;
import dbdr.domain.excel.service.ExcelUploadService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "엑셀-요양보호사,보호자,돌봄대상자", description = "엑셀 다운로드와 업로드")
@RestController
@RequestMapping("/${spring.app.version}/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelDownloadService excelDownloadService;
    private final ExcelUploadService excelUploadService;

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApplicationException(ApplicationError.EMPTY_FILE);
        }
        if (!EXCEL_CONTENT_TYPE.equals(file.getContentType())) {
            throw new ApplicationException(ApplicationError.INVALID_FILE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApplicationException(ApplicationError.FILE_SIZE_EXCEEDED);
        }
    }

    @Operation(summary = "요양관리사 엑셀 다운로드")
    @GetMapping("/careworker/download")
    public ResponseEntity<byte[]> downloadCareworkerTemplate() {
        byte[] data = excelDownloadService.generateCareworkerTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=careworker_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }

    @Operation(summary = "보호자 엑셀 다운로드")
    @GetMapping("/guardian/download")
    public ResponseEntity<byte[]> downloadGuardianTemplate() {
        byte[] data = excelDownloadService.generateGuardianTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guardian_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }

    @Operation(summary = "돌봄대상자 엑셀 다운로드")
    @GetMapping("/recipient/download")
    public ResponseEntity<byte[]> downloadRecipientTemplate() {
        byte[] data = excelDownloadService.generateRecipientTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=recipient_template.xlsx")
                .header("Content-Type", EXCEL_CONTENT_TYPE)
                .body(data);
    }

    @Operation(summary = "요양관리사 엑셀 업로드")
    @PostMapping("/careworker/upload")
    public ResponseEntity<CareworkerFileUploadResponseDto> uploadCareworkerData(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        CareworkerFileUploadResponseDto result = excelUploadService.uploadCareworkerExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "보호자 엑셀 업로드")
    @PostMapping("/guardian/upload")
    public ResponseEntity<GuardianFileUploadResponseDto> uploadGuardianData(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        GuardianFileUploadResponseDto result = excelUploadService.uploadGuardianExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "돌봄대상자 엑셀 업로드")
    @PostMapping("/recipient/upload")
    public ResponseEntity<RecipientFileUploadResponseDto> uploadRecipientData(@RequestParam("file") MultipartFile file) {
        validateFile(file);
        RecipientFileUploadResponseDto result = excelUploadService.uploadRecipientExcel(file);
        return ResponseEntity.ok(result);
    }
}