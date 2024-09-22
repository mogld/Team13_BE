package dbdr.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "careworker")
public class Careworker extends BaseEntity {

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "careworker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recipient> recipients;


    public Careworker(Long institutionId, String name, String email, String phone) {
        this.institutionId = institutionId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void updateCareworker(Long institutionId, String name, String email, String phone) {
        this.institutionId = institutionId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
