package dbdr.domain.careworker.service;

import dbdr.domain.careworker.dto.CareworkerMapper;
import dbdr.domain.careworker.dto.request.CareworkerUpdateRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerMyPageResponseDTO;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CareworkerService {

    private final CareworkerRepository careworkerRepository;
    private final InstitutionService institutionService;
    private final CareworkerMapper careworkerMapper;

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getCareworkersByInstitution(Long institutionId) {
        List<Careworker> results = careworkerRepository.findAllByInstitutionId(institutionId);
        return results.stream().map(careworkerMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerByInstitution(Long careworkerId, Long institutionId) {
        institutionService.getInstitutionById(institutionId);

        Careworker careworker = careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        return careworkerMapper.toResponse(careworker);
    }

    @Transactional(readOnly = true)
    public Careworker getCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public CareworkerResponseDTO getCareworkerResponseById(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return careworkerMapper.toResponse(careworker);
    }

    @Transactional(readOnly = true)
    public List<CareworkerResponseDTO> getAllCareworkers() {
        List<Careworker> careworkers = careworkerRepository.findAll();
        return careworkers.stream().map(careworkerMapper::toResponse).toList();
    }



    @Transactional
    public CareworkerResponseDTO createCareworker(CareworkerRequestDTO careworkerRequestDTO) {
        ensureUniqueEmail(careworkerRequestDTO.getEmail());
        ensureUniquePhone(careworkerRequestDTO.getPhone());

        Careworker careworker = careworkerMapper.toEntity(careworkerRequestDTO);

        careworkerRepository.save(careworker);
        return careworkerMapper.toResponse(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworker(Long careworkerId, CareworkerRequestDTO request) {
        Careworker careworker = findCareworkerById(careworkerId);

        /*if (!careworker.getInstitution().equals(institution)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }*/

        careworker.updateCareworker(careworkerMapper.toEntity(request));
        return careworkerMapper.toResponse(careworker);
    }

    @Transactional
    public CareworkerResponseDTO updateCareworkerByAdmin(Long careworkerId, CareworkerRequestDTO request) {
        Careworker careworker = findCareworkerById(careworkerId);


        Institution institution = institutionService.getInstitutionById(request.getInstitutionId());
        /*if (institution == null) {
            throw new ApplicationException(ApplicationError.INSTITUTION_NOT_FOUND);
        }*/

        careworker.updateInstitution(institution);
        careworker.updateCareworker(careworkerMapper.toEntity(request));

        return careworkerMapper.toResponse(careworker);
    }



    @Transactional
    public void deleteCareworker(Long careworkerId, Long institutionId) {
        Careworker careworker = findCareworkerById(careworkerId);

        if (!careworker.getInstitution().getId().equals(institutionId)) {
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }


        careworkerRepository.delete(careworker);
    }

    @Transactional
    public void deleteCareworkerByAdmin(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        careworkerRepository.delete(careworker);
    }

    @Transactional(readOnly = true)
    public CareworkerMyPageResponseDTO getMyPageInfo(Long careworkerId) {
        Careworker careworker = findCareworkerById(careworkerId);
        return toMyPageResponseDTO(careworker);
    }

    @Transactional
    public CareworkerMyPageResponseDTO updateWorkingDaysAndAlertTime(Long careworkerId, CareworkerUpdateRequestDTO request) {
        Careworker careworker = careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));

        careworker.updateWorkingDays(request.getWorkingDays());
        careworker.updateAlertTime(request.getAlertTime());

        return toMyPageResponseDTO(careworker);
    }


    private Careworker findCareworkerById(Long careworkerId) {
        return careworkerRepository.findById(careworkerId)
                .orElseThrow(() -> new ApplicationException(ApplicationError.CAREWORKER_NOT_FOUND));
    }

    private void ensureUniqueEmail(String email) {
        if (careworkerRepository.existsByEmail(email)) {
            throw new ApplicationException(ApplicationError.DUPLICATE_EMAIL);
        }
    }

    private void ensureUniquePhone(String phone) {
        if (careworkerRepository.findByPhone(phone).isPresent()) {
            throw new ApplicationException(ApplicationError.DUPLICATE_PHONE);
        }
    }

    /*private CareworkerResponseDTO toResponseDTO(Careworker careworker) {
        return new CareworkerResponseDTO(careworker.getId(), careworker.getInstitution().getId(),
                careworker.getName(), careworker.getEmail(), careworker.getPhone());
    }*/

    public Careworker findByLineUserId(String userId) {
        return careworkerRepository.findByLineUserId(userId).orElse(null);
    }

    public Careworker findByPhone(String phoneNumber) {
        return careworkerRepository.findByPhone(phoneNumber).orElse(null);
    }

    private CareworkerMyPageResponseDTO toMyPageResponseDTO(Careworker careworker) {
        return new CareworkerMyPageResponseDTO(
                careworker.getName(),
                careworker.getPhone(),
                careworker.getInstitution().getInstitutionName(),
                careworker.getLoginId(),
                careworker.getWorkingDays(),
                careworker.getAlertTime()
        );
    }
}