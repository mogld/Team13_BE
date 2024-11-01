package dbdr.domain.recipient.controller;

import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.service.RecipientService;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.careworker.service.CareworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@Tag(name = "[관리자] 돌봄대상자 (Recipient)", description = "돌봄대상자 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/institution/{institutionId}/careworker/{careworkerId}/recipient")
public class RecipientController {

    private final RecipientService recipientService;
    private final InstitutionService institutionService;
    private final CareworkerService careworkerService;

    @Value("${spring.app.version}")
    private String appVersion;

    public RecipientController(RecipientService recipientService, InstitutionService institutionService, CareworkerService careworkerService) {
        this.recipientService = recipientService;
        this.institutionService = institutionService;
        this.careworkerService = careworkerService;
    }

    @Operation(summary = "전체 돌봄대상자 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<List<RecipientResponseDTO>> getAllRecipients() {
        List<RecipientResponseDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }

    @Operation(summary = "돌봄대상자 한 사람의 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> getRecipientById(@PathVariable("id") Long id) {
        RecipientResponseDTO recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(recipient);
    }

    @Operation(summary = "돌봄대상자 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<RecipientResponseDTO> createRecipient(
            @PathVariable Long institutionId,
            @PathVariable Long careworkerId,
            @Valid @RequestBody RecipientRequestDTO recipientDTO) {

        var institution = institutionService.getInstitutionById(institutionId);
        var careworker = careworkerService.getCareworkerById(careworkerId);
        RecipientResponseDTO newRecipient = recipientService.createRecipient(recipientDTO, institution, careworker);
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/recipient/" + newRecipient.getId()))
                .body(newRecipient);
    }

    @Operation(summary = "돌봄대상자 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{id}")
    public ResponseEntity<RecipientResponseDTO> updateRecipient(
            @PathVariable("id") Long id,
            @PathVariable Long institutionId,
            @PathVariable Long careworkerId,
            @RequestBody RecipientRequestDTO recipientDTO) {

        var institution = institutionService.getInstitutionById(institutionId);
        var careworker = careworkerService.getCareworkerById(careworkerId);
        RecipientResponseDTO updatedRecipient = recipientService.updateRecipient(id, recipientDTO, institution, careworker);
        return ResponseEntity.ok(updatedRecipient);
    }

    @Operation(summary = "돌봄대상자 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable("id") Long id,
            @PathVariable Long institutionId,
            @PathVariable Long careworkerId) {

        var institution = institutionService.getInstitutionById(institutionId);
        var careworker = careworkerService.getCareworkerById(careworkerId);
        recipientService.deleteRecipient(id, institution, careworker);
        return ResponseEntity.noContent().build();
    }
}