package dbdr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipientDTO {

    private Long id;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 항목입니다.")
    private LocalDate birth;

    @NotBlank(message = "성별은 필수 항목입니다.")
    @Pattern(regexp = "^(남|여)$")
    private String gender;

    @NotBlank(message = "장기요양등급은 필수 항목입니다.")
    private String careLevel;

    @NotBlank(message = "장기요양번호는 필수 항목입니다.")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Care number format is invalid")
    private String careNumber;

    @NotNull(message = "입소일은 필수 항목입니다.")
    private LocalDate startDate;

    @NotBlank(message = "요양기관이름은 필수 항목입니다.")
    private String institution;

    @NotNull(message = "요양기관번호는 필수 항목입니다.")
    private Long institutionNumber;

    private Long careworkerId;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private boolean isDeleted;
}
