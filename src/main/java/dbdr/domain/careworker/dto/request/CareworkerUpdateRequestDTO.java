package dbdr.domain.careworker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class CareworkerUpdateRequestDTO {

    @NotNull(message = "근무일은 필수 항목입니다.")
    private List<LocalDate> workingDays;

    @NotNull(message = "알림 시간은 필수 항목입니다.")
    private LocalTime alertTime;
}