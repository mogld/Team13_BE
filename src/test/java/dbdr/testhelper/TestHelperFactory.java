package dbdr.testhelper;

import com.linecorp.bot.client.LineMessagingClient;
import dbdr.domain.admin.entity.Admin;
import dbdr.domain.admin.repository.AdminRepository;
import dbdr.domain.admin.service.AdminService;
import dbdr.domain.careworker.dto.request.CareworkerRequestDTO;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.careworker.repository.CareworkerRepository;
import dbdr.domain.careworker.service.CareworkerService;
import dbdr.domain.chart.dto.ChartMapper;
import dbdr.domain.chart.entity.Chart;
import dbdr.domain.chart.repository.ChartRepository;
import dbdr.domain.chart.service.ChartService;
import dbdr.domain.guardian.dto.request.GuardianRequest;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.guardian.repository.GuardianRepository;
import dbdr.domain.guardian.service.GuardianService;
import dbdr.domain.institution.dto.request.InstitutionRequest;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.repository.InstitutionRepository;
import dbdr.domain.institution.service.InstitutionService;
import dbdr.domain.recipient.dto.request.RecipientRequestDTO;
import dbdr.domain.recipient.entity.Recipient;
import dbdr.domain.recipient.repository.RecipientRepository;
import dbdr.domain.recipient.service.RecipientService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@TestPropertySource("classpath:application.yml")
@Component
@Transactional
public class TestHelperFactory {

    @MockBean
    private LineMessagingClient lineMessagingClient;

    @Autowired
    GuardianService guardianService;

    @Autowired
    CareworkerService careworkerService;

    @Autowired
    InstitutionService institutionService;

    @Autowired
    ChartService chartService;

    @Autowired
    RecipientService recipientService;

    @Autowired
    AdminService adminService;

    @Autowired
    GuardianRepository guardianRepository;

    @Autowired
    CareworkerRepository careworkerRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    @Autowired
    ChartRepository chartRepository;

    @Autowired
    RecipientRepository recipientRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ChartMapper chartMapper;


    private TestHelper testHelper;

    private List<GuardianRequest> guardians = new ArrayList<>();
    private List<CareworkerRequestDTO> careworkers = new ArrayList<>();
    private List<InstitutionRequest> institutions = new ArrayList<>();
    private List<RecipientRequestDTO> recipients = new ArrayList<>();
    private List<Chart> charts = new ArrayList<>();
    private List<Admin> admins = new ArrayList<>();

    public TestHelper create(Integer port) {
        serviceInit();
        tableCreate();
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port + "/v1")
            .defaultHeaders(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
            .build();
        testHelper = new TestHelper(port, restClient);
        return testHelper;
    }

    private void tableCreate() {
    }

    private void serviceInit() {
        for (Admin admin : admins) {
            adminService.addAdmin(admin);
        }
        for (InstitutionRequest institutionRequest : institutions) {
            institutionService.addInstitution(institutionRequest);
        }

        for (Chart chart : charts) {
            chartRepository.save(chart);
        }

        for (CareworkerRequestDTO careworkerRequestDTO : careworkers) {
            careworkerService.createCareworker(careworkerRequestDTO);
        }

        for (GuardianRequest guardianRequest : guardians) {
            guardianService.addGuardian(guardianRequest);
        }
    }

     public TestHelperFactory addGuardian(Guardian guardian) {
        GuardianRequest guardianRequest = convertGuardian(guardian);
        guardians.add(guardianRequest);
        return this;
    }

    public TestHelperFactory addCareworker(Careworker careworker) {
        CareworkerRequestDTO careworkerRequestDTO = convertCareworker(careworker);
        careworkers.add(careworkerRequestDTO);
        return this;
    }

    public TestHelperFactory addInstitution(Institution institution) {
        InstitutionRequest institutionRequest = convertInstitution(institution);
        institutions.add(institutionRequest);
        return this;
    }

    public TestHelperFactory addRecipient(Recipient recipient) {
        RecipientRequestDTO recipientRequestDTO = convertRecipient(recipient);
        recipients.add(recipientRequestDTO);
        return this;
    }

    /**
     * Request 만들기가 너무 복잡해서 entity로 넣습니다.
     * @param chart
     */
    public TestHelperFactory addChart(Chart chart) {
        charts.add(chart);
        return this;
    }

    public TestHelperFactory addAdmin(Admin admin) {
        admins.add(admin);
        return this;
    }

    private GuardianRequest convertGuardian(Guardian guardian) {
        return new GuardianRequest(guardian.getPhone(), guardian.getName(),
            guardian.getLoginPassword());
    }

    private CareworkerRequestDTO convertCareworker(Careworker careworker) {
        return new CareworkerRequestDTO();
    }

    private InstitutionRequest convertInstitution(Institution institution) {
        return new InstitutionRequest(institution.getInstitutionNumber(), institution.getInstitutionName(),
            institution.getLoginId(), institution.getLoginPassword());
    }

    private RecipientRequestDTO convertRecipient(Recipient recipient) {
        return new RecipientRequestDTO();
    }

}