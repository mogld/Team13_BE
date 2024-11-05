package dbdr.domain.careworker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CareworkerMyPageResponseDTO {

    private String name;
    private String phone;
    private Long institutionNumber;
    private String loginId;
    private List<LocalDate> workingDays;
    private LocalTime alertTime;
}