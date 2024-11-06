package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.security.LoginCareworker;
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


@Tag(name = "[돌봄대상자] 요양보호사 권한", description = "요양보호사가 담당하는 돌봄대상자 정보 조회, 추가, 수정, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/careworker/recipient")
@RequiredArgsConstructor
public class RecipientCareworkerController {

    private final RecipientService recipientService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "담당 돌봄대상자 전체 조회 ", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<Page<RecipientResponseDTO>> getAllRecipients(
            @LoginCareworker Careworker careworker,
            Pageable pageable) {
        Page<RecipientResponseDTO> recipients = recipientService.getRecipientsByCareworker(careworker.getId(), pageable);
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "담당 돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker) {
        RecipientResponseDTO recipient = recipientService.getRecipientByCareworker(recipientId, careworker.getId());
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "담당 돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(
            @Valid @RequestBody RecipientRequestDTO recipientDTO,
            @LoginCareworker Careworker careworker) {
        RecipientResponseDTO newRecipient = recipientService.createRecipientForCareworker(recipientDTO, careworker.getId());
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/careworker/"+ careworker.getId() + "/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "담당 돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipientForCareworker(recipientId, recipientDTO, careworker.getId());
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "담당 돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable("recipientId") Long recipientId,
            @LoginCareworker Careworker careworker) {
        recipientService.deleteRecipientForCareworker(recipientId, careworker.getId());
        return ResponseEntity.noContent().build();
    }
}