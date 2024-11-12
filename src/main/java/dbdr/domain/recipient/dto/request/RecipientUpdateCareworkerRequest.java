package dbdr.domain.recipient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipientUpdateCareworkerRequest {

    @Schema(description = "돌봄대상자 이름", example = "이순자")
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @Schema(description = "돌봄대상자의 생년월일", example = "1981-08-01")
    @NotNull(message = "생년월일은 필수 항목입니다.")
    private LocalDate birth;

    @Schema(description = "돌봄대상자의 성별", example = "여")
    @NotBlank(message = "성별은 필수 항목입니다.")
    @Pattern(regexp = "^(남|여)$")
    private String gender;

    @Schema(description = "장기요양등급", example = "2등급")
    @NotBlank(message = "장기요양등급은 필수 항목입니다.")
    private String careLevel;

    @Schema(description = "장기요양번호", example = "123400000")
    @NotBlank(message = "장기요양번호는 필수 항목입니다.")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "올바르지 않은 형식입니다.")
    private String careNumber;

    @Schema(description = "입소일", example = "2024-01-01")
    @NotNull(message = "입소일은 필수 항목입니다.")
    private LocalDate startDate;

    // institutionId, careworkerId, guardianId는 수정 불가
}