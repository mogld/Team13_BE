package dbdr.domain.recipient.repository;

import dbdr.domain.recipient.entity.Recipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    boolean existsByCareNumber(String careNumber);


    Page<Recipient> findByCareworkerId(Long careworkerId, Pageable pageable);

    Page<Recipient> findByInstitutionId(Long institutionId, Pageable pageable);

    Optional<Recipient> findByIdAndCareworkerId(Long recipientId, Long careworkerId);

    Optional<Recipient> findByIdAndInstitutionId(Long recipientId, Long institutionId);
}