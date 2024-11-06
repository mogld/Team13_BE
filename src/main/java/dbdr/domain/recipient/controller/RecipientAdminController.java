package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "[관리자] 돌봄대상자 관리", description = "관리자가 관리하는 모든 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/admin/recipient")
@RequiredArgsConstructor
public class RecipientAdminController {

    private final RecipientService recipientService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "전체 돌봄대상자 조회 (페이징)")
    @GetMapping
    public ResponseEntity<Page<RecipientResponseDTO>> getAllRecipients(Pageable pageable) {
        Page<RecipientResponseDTO> recipients = recipientService.getAllRecipients(pageable);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "돌봄대상자 정보 조회")
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(@PathVariable("recipientId") Long recipientId) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(recipientId);
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "돌봄대상자 추가")
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(@Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/admin/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "돌봄대상자 정보 수정")
    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipient(recipientId, recipientDTO);
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "돌봄대상자 삭제")
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable("recipientId") Long recipientId) {
        recipientService.deleteRecipient(recipientId);
        return ResponseEntity.noContent().build();
    }
}