package dbdr.controller;

import dbdr.dto.RecipientDTO;
import dbdr.service.RecipientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;

    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @GetMapping
    public ResponseEntity<List<RecipientDTO>> getAllRecipients() {
        List<RecipientDTO> recipients = recipientService.getAllRecipients();
        return ResponseEntity.ok(recipients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipientDTO> getRecipientById(@PathVariable Long id) {
        RecipientDTO recipient = recipientService.getRecipientById(id);
        return ResponseEntity.ok(recipient);
    }

    @PostMapping
    public ResponseEntity<RecipientDTO> createRecipient(@RequestBody RecipientDTO recipientDTO) {
        RecipientDTO newRecipient = recipientService.createRecipient(recipientDTO);
        return ResponseEntity.ok(newRecipient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipientDTO> updateRecipient(@PathVariable Long id, @RequestBody RecipientDTO recipientDTO) {
        RecipientDTO updatedRecipient = recipientService.updateRecipient(id, recipientDTO);
        return ResponseEntity.ok(updatedRecipient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipient(@PathVariable Long id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.noContent().build();
    }
}
