package dbdr.domain.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRecipientResponseDto {

    private Long id;
    private String name;
    private LocalDate birth;
    private String gender;
    private String careLevel;
    private String careNumber;
    private LocalDate startDate;
    private String institution;
}