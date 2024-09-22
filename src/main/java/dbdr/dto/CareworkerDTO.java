package dbdr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareworkerDTO {

    private Long id;

    @NotNull
    private Long institutionId;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바르지 않은 형식입니다.")
    private String email;

    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private boolean isActive;
}