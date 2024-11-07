package dbdr.domain.recipient.service;

import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.dto.response.RecipientResponseDTO;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final CareworkerService careworkerService;
    private final InstitutionService institutionService;

    // 전체 돌봄대상자 목록 조회 (관리자용)
    public List<RecipientResponseDTO> getAllRecipients() {
        return recipientRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // 특정 돌봄대상자 조회 (관리자용)
    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientById(Long recipientId) {
        Recipient recipient = findRecipientById(recipientId);
        return toResponseDTO(recipient);
    }

    // 새로운 돌봄대상자 추가 (관리자용)
    @Transactional
    public RecipientResponseDTO createRecipient(RecipientRequestDTO recipientDTO) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());
        Institution institution = institutionService.getInstitutionById(recipientDTO.getInstitutionId());

        Recipient recipient = new Recipient(recipientDTO, institution, careworker);
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    // 돌봄대상자 정보 수정 (관리자용)
    @Transactional
    public RecipientResponseDTO updateRecipient(Long recipientId, RecipientRequestDTO recipientDTO) {
        Recipient recipient = findRecipientById(recipientId);
        recipient.updateRecipient(recipientDTO);
        return toResponseDTO(recipient);
    }

    // 돌봄대상자 삭제 (관리자용)
    @Transactional
    public void deleteRecipient(Long recipientId) {
        Recipient recipient = findRecipientById(recipientId);
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    //전체 돌봄대상자 목록 조회 (요양보호사용)
    @Transactional(readOnly = true)
    public List<RecipientResponseDTO> getRecipientsByCareworker(Long careworkerId) {
        return recipientRepository.findByCareworkerId(careworkerId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }


    //전체 돌봄대상자 목록 조회 (요양원용)
    public List<RecipientResponseDTO> getRecipientsByInstitution(Long institutionId) {
        return recipientRepository.findByInstitutionId(institutionId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    //요양보호사가 담당하는 특정 돌봄대상자 정보 조회
    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientByCareworker(Long recipientId, Long careworkerId) {
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);
        return toResponseDTO(recipient);
    }

    //요양원이 관리하는 특정 돌봄대상자 정보 조회
    @Transactional(readOnly = true)
    public RecipientResponseDTO getRecipientByInstitution(Long recipientId, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        return toResponseDTO(recipient);
    }

    //요양보호사가 새로운 돌봄대상자를 추가
    @Transactional
    public RecipientResponseDTO createRecipientForCareworker(RecipientRequestDTO recipientDTO, Long careworkerId) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Careworker careworker = careworkerService.getCareworkerById(careworkerId);
        Recipient recipient = new Recipient(recipientDTO, careworker);
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    //요양원이 새로운 돌봄대상자(요양보호사 배정 필수)를 추가
    @Transactional
    public RecipientResponseDTO createRecipientForInstitution(RecipientRequestDTO recipientDTO, Long institutionId) {
        ensureUniqueCareNumber(recipientDTO.getCareNumber());
        Institution institution = institutionService.getInstitutionById(institutionId);
        Careworker careworker = careworkerService.getCareworkerById(recipientDTO.getCareworkerId());

        Recipient recipient = new Recipient(recipientDTO, institution, careworker);
        recipientRepository.save(recipient);
        return toResponseDTO(recipient);
    }

    //요양보호사가 담당하는 돌봄대상자 정보 수정
    @Transactional
    public RecipientResponseDTO updateRecipientForCareworker(Long recipientId, RecipientRequestDTO recipientDTO, Long careworkerId) {
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);
        recipient.updateRecipient(recipientDTO);
        return toResponseDTO(recipient);
    }

    //요양원에 속한 돌봄대상자 정보 수정
    @Transactional
    public RecipientResponseDTO updateRecipientForInstitution(Long recipientId, RecipientRequestDTO recipientDTO, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        recipient.updateRecipient(recipientDTO);
        return toResponseDTO(recipient);
    }

    //요양보호사가 담당하는 돌봄대상자 삭제
    @Transactional
    public void deleteRecipientForCareworker(Long recipientId, Long careworkerId) {
        Recipient recipient = findRecipientByIdAndCareworker(recipientId, careworkerId);
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    //요양원에 속한 돌봄대상자 삭제
    @Transactional
    public void deleteRecipientForInstitution(Long recipientId, Long institutionId) {
        Recipient recipient = findRecipientByIdAndInstitution(recipientId, institutionId);
        recipient.deactivate();
        recipientRepository.delete(recipient);
    }

    // 권한별 접근 검증 로직
    private Recipient findRecipientByIdAndCareworker(Long recipientId, Long careworkerId) {
        return recipientRepository.findByIdAndCareworkerId(recipientId, careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED));
    }

    private Recipient findRecipientByIdAndInstitution(Long recipientId, Long institutionId) {
        return recipientRepository.findByIdAndInstitutionId(recipientId, institutionId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED));
    }

    public Recipient findRecipientById(Long recipientId) {
        return recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.RECIPIENT_NOT_FOUND));
    }

    private void ensureUniqueCareNumber(String careNumber) {
        if (recipientRepository.existsByCareNumber(careNumber)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_CARE_NUMBER);
        }
    }

    private RecipientResponseDTO toResponseDTO(Recipient recipient) {
        return new RecipientResponseDTO(
                recipient.getId(),
                recipient.getName(),
                recipient.getBirth(),
                recipient.getGender(),
                recipient.getCareLevel(),
                recipient.getCareNumber(),
                recipient.getStartDate(),
                recipient.getInstitution().getInstitutionName(),
                recipient.getInstitution().getInstitutionNumber(),
                recipient.getInstitution().getId(),
                recipient.getCareworker() != null ? recipient.getCareworker().getId() : null
        );
    }
}