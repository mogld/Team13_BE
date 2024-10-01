package dbdr.domain.recipient.entity;

import dbdr.domain.core.entity.BaseEntity;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "recipients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE recipients SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Recipient extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String careLevel;

    @Column(nullable = false, unique = true)
    private String careNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private String institution;

    @Column(nullable = false)
    private Long institutionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careworker_id")
    private Careworker careworker;

    @Builder
    public Recipient(String name,
        LocalDate birth,
        String gender,
        String careLevel,
        String careNumber,
        LocalDate startDate,
        String institution,
        Long institutionNumber,
        Careworker careworker) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.careLevel = careLevel;
        this.careNumber = careNumber;
        this.startDate = startDate;
        this.institution = institution;
        this.institutionNumber = institutionNumber;
        this.careworker = careworker;
    }


    public void updateRecipient(RecipientRequestDTO recipientDTO) {
        this.name = recipientDTO.getName();
        this.birth = recipientDTO.getBirth();
        this.gender = recipientDTO.getGender();
        this.careLevel = recipientDTO.getCareLevel();
        this.careNumber = recipientDTO.getCareNumber();
        this.startDate = recipientDTO.getStartDate();
        this.institution = recipientDTO.getInstitution();
        this.institutionNumber = recipientDTO.getInstitutionNumber();
        //this.careworker = careworker;
    }

}