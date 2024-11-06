package dbdr.domain.careworker.dto.request;

import dbdr.domain.careworker.entity.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

@Getter
public class CareworkerUpdateRequestDTO {

    @NotNull(message = "근무일은 필수 항목입니다.")
    private Set<DayOfWeek> workingDays;


    @NotNull(message = "알림 시간은 필수 항목입니다.")
    private LocalTime alertTime;
}