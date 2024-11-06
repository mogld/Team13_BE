package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "[요양원] 돌봄대상자 관리", description = "요양원이 관리하는 모든 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/institution/recipient")
@RequiredArgsConstructor
public class RecipientInstitutionController {

    private final RecipientService recipientService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "전체 돌봄대상자 조회 (페이징)", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<Page<RecipientResponseDTO>> getAllRecipients(
            @RequestParam("institutionId") Long institutionId,
            Pageable pageable) {
        Page<RecipientResponseDTO> recipients = recipientService.getRecipientsByInstitution(institutionId, pageable);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @RequestParam("institutionId") Long institutionId) {
        RecipientResponseDTO recipient = recipientService.getRecipientByInstitution(recipientId, institutionId);
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(
            @RequestParam("institutionId") Long institutionId,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipientForInstitution(recipientDTO, institutionId);
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/institution/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @RequestParam("institutionId") Long institutionId,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipientForInstitution(recipientId, recipientDTO, institutionId);
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable("recipientId") Long recipientId,
            @RequestParam("institutionId") Long institutionId) {
        recipientService.deleteRecipientForInstitution(recipientId, institutionId);
        return ResponseEntity.noContent().build();
    }
}