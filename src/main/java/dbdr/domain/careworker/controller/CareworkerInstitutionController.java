package dbdr.domain.careworker.controller;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.service.CareworkerService;
import static dbdr.global.util.api.Utils.DEFAULT_PAGE_SIZE;

import dbdr.global.util.api.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

//요양원 권한 추가
@Tag(name = "[요양원] 요양보호사 관리  ", description = "요양보호사 정보 조회, 수정, 삭제, 추가")
@RestController
@RequestMapping("/${spring.app.version}/institution/careworker")
@RequiredArgsConstructor
public class CareworkerInstitutionController {

    private final CareworkerService careworkerService;

    @Value("${spring.app.version}")
    private String appVersion;

    @Operation(summary = "특정 요양원아이디로 전체 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/institution")
    public ResponseEntity<ApiUtils.ApiResult<Page<CareworkerResponseDTO>>> getAllCareworkers(
            @Parameter(hidden = true)
            @RequestParam("institutionId") @NotNull Long institutionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CareworkerResponseDTO> institutions = careworkerService.getCareworkersByInstitution(institutionId,pageable);
        return ResponseEntity.ok(ApiUtils.success(institutions)) ;
    }

    @Operation(summary = "요양보호사 아이디로 요양보호사 정보 조회", security = @SecurityRequirement(name = "JWT"))
    @GetMapping("/{careworkerId}")
    public ResponseEntity<CareworkerResponseDTO> getCareworkerById(
            @PathVariable("careworkerId") Long careworkerId,
            @RequestParam("institutionId") Long institutionId) {
        CareworkerResponseDTO careworker = careworkerService.getCareworkerByInstitution(careworkerId, institutionId);
        return ResponseEntity.ok(careworker);
    }


    @Operation(summary = "요양보호사 추가", security = @SecurityRequirement(name = "JWT"))
    @PostMapping
    public ResponseEntity<CareworkerResponseDTO> createCareworker(
            @RequestParam("institutionId") Long institutionId,
            @Valid @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO newCareworker = careworkerService.createCareworker(careworkerDTO, institutionId);
        return ResponseEntity.created(
                        URI.create("/" + appVersion + "/institution/careworker/" + newCareworker.getId()))
                .body(newCareworker);
    }


    @Operation(summary = "요양보호사 정보 수정", security = @SecurityRequirement(name = "JWT"))
    @PutMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<CareworkerResponseDTO>> updateCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId,
            @RequestBody CareworkerRequestDTO careworkerDTO) {
        CareworkerResponseDTO updatedCareworker = careworkerService.updateCareworker(careworkerId, careworkerDTO, institutionId);
        return ResponseEntity.ok(ApiUtils.success(updatedCareworker));
    }

    @Operation(summary = "요양보호사 삭제", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{careworkerId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> deleteCareworker(
            @PathVariable Long careworkerId,
            @RequestParam("institutionId") @NotNull Long institutionId) {
        careworkerService.deleteCareworker(careworkerId, institutionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}