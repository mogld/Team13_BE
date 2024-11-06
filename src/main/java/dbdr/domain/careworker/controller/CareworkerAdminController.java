package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "[관리자] 요양보호사 관리", description = "관리자가 요양사 정보를 조회, 수정, 추가, 삭제")
@RestController
@RequestMapping("/${spring.app.version}/admin/careworker")
@RequiredArgsConstructor
public class CareworkerAdminController {

    private final CareworkerService careworkerService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "요양보호사 정보 조회")
    @GetMapping("/{careworkerId}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
            @PathVariable Long careworkerId) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerResponseById(careworkerId);
        return ResponseEntity.ok(careworker);
    }

    @Operation(summary = "요양보호사 추가")
    @PostMapping
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO, careworkerDTO.getInstitutionId());
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/admin/careworker/" + newCareworker.getId()))
                .body(newCareworker);

    }


    @Operation(summary = "요양보호사 정보 수정")
    @PutMapping("/{careworkerId}")
    public ResponseEntity<CareworkerResponseDTO> updateCareworker(
            @PathVariable Long careworkerId,
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(careworkerId, careworkerDTO, careworkerDTO.getInstitutionId());
        return ResponseEntity.ok(updatedCareworker);
    }

    @Operation(summary = "요양보호사 삭제")
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<Void> deleteCareworker(@PathVariable Long careworkerId) {
        careworkerService.deleteCareworker(careworkerId, null); // institutionId는 필요하지 않음
        return ResponseEntity.noContent().build();
    }
}
