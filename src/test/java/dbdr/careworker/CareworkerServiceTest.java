package dbdr.careworker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.dto.request.CareworkerUpdateRequestDTO;
import dbdr.domain.careworker.dto.response.CareworkerMyPageResponseDTO;
import dbdr.domain.careworker.dto.response.CareworkerResponseDTO;
import dbdr.domain.careworker.entity.Careworker;

import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class CareworkerServiceTest {

    @Autowired
    private CareworkerService careworkerService;

    @MockBean
    private InstitutionService institutionService;

    @MockBean
    private CareworkerRepository careworkerRepository;

    private CareworkerRequestDTO careworkerRequestDTO;
    private Careworker careworker;
    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = Institution.builder()
                .institutionNumber(100L)
                .institutionName("HealthCare Institution")
                .build();

        // ReflectionTestUtils를 사용하여 institution 엔티티의 id 설정
        setField(institution, "id", 1L);

        careworker = Careworker.builder()
                .institution(institution)
                .name("John Doe")
                .email("johndoe@email.com")
                .phone("01012345678")
                .build();

        // Careworker의 id 설정
        setField(careworker, "id", 1L);

        careworkerRequestDTO = new CareworkerRequestDTO(1L, "John Doe", "johndoe@email.com", "01012345678");
    }

    @Test
    void testGetCareworkerById_Success() {
        // Test: 특정 ID로 요양보호사 정보 조회 성공 시 반환값 검증
        when(careworkerRepository.findById(1L)).thenReturn(Optional.of(careworker));

        CareworkerResponseDTO response = careworkerService.getCareworkerResponseById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("johndoe@email.com");
    }


    @Test
    void testCreateCareworker_Success() {
        // Test: 요양보호사 생성 성공 시 반환값 검증
        when(institutionService.getInstitutionById(1L)).thenReturn(institution);
        when(careworkerRepository.save(any(Careworker.class))).thenReturn(careworker);

        CareworkerResponseDTO response = careworkerService.createCareworker(careworkerRequestDTO, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        verify(careworkerRepository).save(any(Careworker.class));
    }



    @Test
    void testUpdateCareworker_Success() {
        // Test: 요양보호사 정보 수정 성공 시 반환값 검증
        when(careworkerRepository.findById(1L)).thenReturn(Optional.of(careworker));
        when(institutionService.getInstitutionById(1L)).thenReturn(institution);

        CareworkerRequestDTO updateRequest = new CareworkerRequestDTO(1L, "Jane Doe", "janedoe@email.com", "01087654321");
        CareworkerResponseDTO response = careworkerService.updateCareworker(1L, updateRequest, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Jane Doe");
        verify(careworkerRepository).findById(1L);
    }



    @Test
    void testDeleteCareworker_Success() {
        // Test: 요양보호사 삭제 성공 시 isActive 필드가 false로 설정되는지 검증
        when(careworkerRepository.findById(1L)).thenReturn(Optional.of(careworker));

        careworkerService.deleteCareworker(1L, 1L);

        verify(careworkerRepository).delete(careworker);
    }



    @Test
    void testGetMyPageInfo_Success() {
        // Test: 본인의 마이페이지 정보를 조회하고 반환값 검증
        when(careworkerRepository.findById(1L)).thenReturn(Optional.of(careworker));

        CareworkerMyPageResponseDTO response = careworkerService.getMyPageInfo(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
    }

    @Test
    void testUpdateWorkingDaysAndAlertTime_Success() {
        // Arrange
        when(careworkerRepository.findById(1L)).thenReturn(Optional.of(careworker));

        // 업데이트할 요일과 알림 시간 설정
        Set<DayOfWeek> newWorkingDays = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        LocalTime newAlertTime = LocalTime.of(9, 0);

        // 업데이트 요청 DTO 생성
        CareworkerUpdateRequestDTO request = new CareworkerUpdateRequestDTO(newWorkingDays, newAlertTime);

        // Act
        CareworkerMyPageResponseDTO response = careworkerService.updateWorkingDaysAndAlertTime(1L, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getWorkingDays()).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        assertThat(response.getAlertTime()).isEqualTo(LocalTime.of(9, 0));

        // Verify that the repository save was called with the updated entity
        verify(careworkerRepository).save(careworker);
    }
}
