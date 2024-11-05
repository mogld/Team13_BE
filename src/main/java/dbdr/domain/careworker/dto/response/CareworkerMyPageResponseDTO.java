package dbdr.domain.careworker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CareworkerMyPageResponseDTO {

    private String name;
    private String phone;
    private String institutionName;
    private String loginId;
    private List<String> workingDays;
    private LocalTime alertTime;
}
