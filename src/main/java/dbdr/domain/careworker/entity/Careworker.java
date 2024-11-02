package dbdr.domain.careworker.entity;

import dbdr.domain.core.entity.BaseEntity;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "careworkers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE careworkers SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Careworker extends BaseEntity {

    @Column(unique = true)
    private String loginId;

    private String loginPassword;

    @Column
    private Long institutionId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    //이게 나중에는 institution객체로 바뀌는 그런 부분
    @Builder
    public Careworker(Long institutionId, String name, String email, String phone) {
        this.institutionId = institutionId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void updateCareworker(CareworkerRequestDTO careworkerDTO) {
        //this.institutionId = careworkerDTO.getInstitutionId();
        this.name = careworkerDTO.getName();
        this.email = careworkerDTO.getEmail();
        this.phone = careworkerDTO.getPhone();
    }
}
