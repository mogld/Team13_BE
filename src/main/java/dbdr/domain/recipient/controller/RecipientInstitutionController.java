package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.domain.institution.entity.Institution;
import dbdr.security.LoginInstitution;
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

@Tag(name = "[돌봄대상자] 요양원 권한", description = "요양원이 관리하는 모든 돌봄대상자 정보 조회, 추가, 수정, 삭제")
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
            @LoginInstitution Institution institution,
            Pageable pageable) {
        Page<RecipientResponseDTO> recipients = recipientService.getRecipientsByInstitution(institution.getId(), pageable);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution) {
        RecipientResponseDTO recipient = recipientService.getRecipientByInstitution(recipientId, institution.getId());
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(
            @Valid @RequestBody RecipientRequestDTO recipientDTO,
            @LoginInstitution Institution institution) {
        RecipientResponseDTO newRecipient = recipientService.createRecipientForInstitution(recipientDTO, institution.getId());
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/institution/"+ institution.getId() + "/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipientForInstitution(recipientId, recipientDTO, institution.getId());
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginInstitution Institution institution) {
        recipientService.deleteRecipientForInstitution(recipientId, institution.getId());
        return ResponseEntity.noContent().build();
    }
}