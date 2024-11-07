package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.entity.Institution;
import dbdr.global.util.api.ApiUtils;
import dbdr.security.LoginInstitution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//요양원 권한 추가
@Tag(name = "[요양원] 요양보호사 관리  ", description = "요양보호사 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/institution/careworker")
@RequiredArgsConstructor
public class CareworkerInstitutionController {

    private final CareworkerService careworkerService;

    @Operation(summary = "특정 요양원아이디로 전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/institution")
    public ResponseEntity<ApiUtils.ApiResult<List<CareworkerResponseDTO>>> getAllCareworkers(
            @LoginInstitution Institution institution) {
        List<CareworkerResponseDTO> institutions = careworkerService.getCareworkersByInstitution(institution.getId());
        return ResponseEntity.ok(ApiUtils.success(institutions)) ;
    }

    @Operation(summary = "요양보호사 아이디로 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> getCareworkerById(
            @PathVariable("careworkerId") Long careworkerId,
            @LoginInstitution Institution institution) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerByInstitution(careworkerId, institution.getId());
        return ResponseEntity.ok(ApiUtils.success(careworker)) ;
    }


    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> createCareworker(
            @LoginInstitution Institution institution,
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO, institution.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiUtils.success(newCareworker));

    }


    @Operation(summary = "요양보호사 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> updateCareworker(
            @PathVariable Long careworkerId,
            @LoginInstitution Institution institution,
            @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(careworkerId, careworkerDTO, institution.getId());
        return ResponseEntity.ok(ApiUtils.success(updatedCareworker));
    }

    @Operation(summary = "요양보호사 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteCareworker(
            @PathVariable Long careworkerId,
            @LoginInstitution Institution institution) {
        careworkerService.deleteCareworker(careworkerId, institution.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}