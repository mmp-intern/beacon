package com.mmp.beacon.commute.application;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import com.mmp.beacon.commute.application.command.BeaconData;
import com.mmp.beacon.commute.domain.AttendanceStatus;
import com.mmp.beacon.commute.domain.Commute;
import com.mmp.beacon.commute.domain.WorkStatus;
import com.mmp.beacon.commute.domain.repository.CommuteRepository;
import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.gateway.domain.Gateway;
import com.mmp.beacon.gateway.domain.repository.GatewayRepository;
import com.mmp.beacon.user.domain.User;
import com.mmp.beacon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CommuteServiceTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommuteRepository commuteRepository;

    @Mock
    private BeaconRepository beaconRepository;

    @Mock
    private TimeService timeService;

    @InjectMocks
    private CommuteService commuteService;

    @Captor
    private ArgumentCaptor<Commute> commuteCapture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주어진 게이트웨이 MAC 주소와 비콘 데이터 리스트를 사용하여 기존 출퇴근 기록이 없는 경우 새로운 출퇴근 기록을 생성한다.")
    void testMarkPresent() {
        // Given
        String gatewayMac1 = "40D63CD6FD92";
        String gatewayMac2 = "50D63CD6FD93";

        BeaconData beaconData1 = new BeaconData("F4741C781187", LocalDateTime.parse("2024-07-23T08:00:00"), LocalDateTime.parse("2024-07-23T08:01:00"));
        BeaconData beaconData2 = new BeaconData("DE42759B6E12", LocalDateTime.parse("2024-07-23T08:01:00"), LocalDateTime.parse("2024-07-23T08:02:00"));
        BeaconData beaconData3 = new BeaconData("A1741C781187", LocalDateTime.parse("2024-07-23T08:02:00"), LocalDateTime.parse("2024-07-23T08:03:00"));
        BeaconData beaconData4 = new BeaconData("BE42759B6E12", LocalDateTime.parse("2024-07-23T08:03:00"), LocalDateTime.parse("2024-07-23T08:04:00"));

        Gateway gateway1 = mock(Gateway.class);
        Gateway gateway2 = mock(Gateway.class);
        Company company = mock(Company.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        User user4 = mock(User.class);
        Beacon beacon1 = mock(Beacon.class);
        Beacon beacon2 = mock(Beacon.class);
        Beacon beacon3 = mock(Beacon.class);
        Beacon beacon4 = mock(Beacon.class);

        when(timeService.nowDate()).thenReturn(LocalDate.of(2024, 7, 23));
        when(gatewayRepository.findByMacAddr(gatewayMac1)).thenReturn(Optional.of(gateway1));
        when(gatewayRepository.findByMacAddr(gatewayMac2)).thenReturn(Optional.of(gateway2));
        when(beaconRepository.findByMacAddr("F4741C781187")).thenReturn(Optional.of(beacon1));
        when(beaconRepository.findByMacAddr("DE42759B6E12")).thenReturn(Optional.of(beacon2));
        when(beaconRepository.findByMacAddr("A1741C781187")).thenReturn(Optional.of(beacon3));
        when(beaconRepository.findByMacAddr("BE42759B6E12")).thenReturn(Optional.of(beacon4));
        when(beacon1.getUser()).thenReturn(user1);
        when(beacon2.getUser()).thenReturn(user2);
        when(beacon3.getUser()).thenReturn(user3);
        when(beacon4.getUser()).thenReturn(user4);
        when(gateway1.getCompany()).thenReturn(company);
        when(gateway2.getCompany()).thenReturn(company);
        when(user1.getCompany()).thenReturn(company);
        when(user2.getCompany()).thenReturn(company);
        when(user3.getCompany()).thenReturn(company);
        when(user4.getCompany()).thenReturn(company);
        when(company.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(company.getEndTime()).thenReturn(LocalTime.of(18, 0));

        // When
        commuteService.processAttendance(gatewayMac1, List.of(beaconData1, beaconData2));
        commuteService.processAttendance(gatewayMac2, List.of(beaconData3, beaconData4));

        // Then
        verify(commuteRepository, times(4)).save(commuteCapture.capture());
        List<Commute> savedCommutes = commuteCapture.getAllValues();

        // Assert each saved Commute
        Commute commute1 = savedCommutes.get(0);
        assertEquals(user1, commute1.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute1.getDate());
        assertEquals(LocalTime.of(8, 0), commute1.getStartedAt());
        assertEquals(LocalTime.of(8, 1), commute1.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute1.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute1.getWorkStatus());

        Commute commute2 = savedCommutes.get(1);
        assertEquals(user2, commute2.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute2.getDate());
        assertEquals(LocalTime.of(8, 1), commute2.getStartedAt());
        assertEquals(LocalTime.of(8, 2), commute2.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute2.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute2.getWorkStatus());

        Commute commute3 = savedCommutes.get(2);
        assertEquals(user3, commute3.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute3.getDate());
        assertEquals(LocalTime.of(8, 2), commute3.getStartedAt());
        assertEquals(LocalTime.of(8, 3), commute3.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute3.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute3.getWorkStatus());

        Commute commute4 = savedCommutes.get(3);
        assertEquals(user4, commute4.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute4.getDate());
        assertEquals(LocalTime.of(8, 3), commute4.getStartedAt());
        assertEquals(LocalTime.of(8, 4), commute4.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute4.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute4.getWorkStatus());
    }

    @Test
    @DisplayName("주어진 회사 ID로 지각자를 기록한다.")
    void testMarkLateArrivals() {
        // Given
        Long companyId = 1L;
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Company company = mock(Company.class);

        when(timeService.nowDate()).thenReturn(LocalDate.of(2024, 7, 23));
        when(userRepository.findByCompanyId(companyId)).thenReturn(List.of(user1, user2));
        when(commuteRepository.findByUserAndDate(user1, LocalDate.of(2024, 7, 23))).thenReturn(Optional.empty());
        when(commuteRepository.findByUserAndDate(user2, LocalDate.of(2024, 7, 23))).thenReturn(Optional.empty());
        when(user1.getCompany()).thenReturn(company);
        when(user2.getCompany()).thenReturn(company);
        when(company.getStartTime()).thenReturn(LocalTime.of(9, 0));

        // When
        commuteService.markLateArrivals(companyId);

        // Then
        verify(commuteRepository, times(2)).save(commuteCapture.capture());
        List<Commute> savedCommutes = commuteCapture.getAllValues();

        // Assert each saved Commute
        Commute commute1 = savedCommutes.get(0);
        assertEquals(user1, commute1.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute1.getDate());
        assertNull(commute1.getStartedAt());
        assertNull(commute1.getEndedAt());
        assertEquals(AttendanceStatus.LATE, commute1.getAttendanceStatus());
        assertEquals(WorkStatus.OUT_OFF_OFFICE, commute1.getWorkStatus());

        Commute commute2 = savedCommutes.get(1);
        assertEquals(user2, commute2.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute2.getDate());
        assertNull(commute2.getStartedAt());
        assertNull(commute2.getEndedAt());
        assertEquals(AttendanceStatus.LATE, commute2.getAttendanceStatus());
        assertEquals(WorkStatus.OUT_OFF_OFFICE, commute2.getWorkStatus());
    }

    @Test
    @DisplayName("주어진 게이트웨이 MAC 주소와 비콘 데이터 리스트를 사용하여 기존 출퇴근 기록이 있는 경우 이를 갱신한다.")
    void testUpdateExistingCommute() {
        // Given
        String gatewayMac = "40D63CD6FD92";
        BeaconData beaconData1 = new BeaconData("F4741C781187", LocalDateTime.parse("2024-07-23T09:00:00"), LocalDateTime.parse("2024-07-23T09:01:00"));
        BeaconData beaconData2 = new BeaconData("DE42759B6E12", LocalDateTime.parse("2024-07-23T09:01:00"), LocalDateTime.parse("2024-07-23T09:02:00"));

        Gateway gateway = mock(Gateway.class);
        Company company = mock(Company.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Beacon beacon1 = mock(Beacon.class);
        Beacon beacon2 = mock(Beacon.class);

        when(timeService.nowDate()).thenReturn(LocalDate.of(2024, 7, 23));
        when(gatewayRepository.findByMacAddr(gatewayMac)).thenReturn(Optional.of(gateway));
        when(beaconRepository.findByMacAddr("F4741C781187")).thenReturn(Optional.of(beacon1));
        when(beaconRepository.findByMacAddr("DE42759B6E12")).thenReturn(Optional.of(beacon2));
        when(beacon1.getUser()).thenReturn(user1);
        when(beacon2.getUser()).thenReturn(user2);
        when(gateway.getCompany()).thenReturn(company);
        when(user1.getCompany()).thenReturn(company);
        when(user2.getCompany()).thenReturn(company);
        when(company.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(company.getEndTime()).thenReturn(LocalTime.of(18, 0));
        when(commuteRepository.findByUserAndDate(user1, LocalDate.of(2024, 7, 23))).thenReturn(Optional.of(new Commute(user1, LocalDate.of(2024, 7, 23), LocalTime.of(8, 0), LocalTime.of(8, 1), AttendanceStatus.PRESENT, WorkStatus.IN_OFFICE)));
        when(commuteRepository.findByUserAndDate(user2, LocalDate.of(2024, 7, 23))).thenReturn(Optional.of(new Commute(user2, LocalDate.of(2024, 7, 23), LocalTime.of(8, 1), LocalTime.of(8, 2), AttendanceStatus.PRESENT, WorkStatus.IN_OFFICE)));

        // When
        commuteService.processAttendance(gatewayMac, List.of(beaconData1, beaconData2));

        // Then
        verify(commuteRepository, times(2)).save(commuteCapture.capture());
        List<Commute> savedCommutes = commuteCapture.getAllValues();

        // Assert each saved Commute
        Commute commute1 = savedCommutes.get(0);
        assertEquals(user1, commute1.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute1.getDate());
        assertEquals(LocalTime.of(8, 0), commute1.getStartedAt());
        assertEquals(LocalTime.of(9, 1), commute1.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute1.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute1.getWorkStatus());

        Commute commute2 = savedCommutes.get(1);
        assertEquals(user2, commute2.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), commute2.getDate());
        assertEquals(LocalTime.of(8, 1), commute2.getStartedAt());
        assertEquals(LocalTime.of(9, 2), commute2.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, commute2.getAttendanceStatus());
        assertEquals(WorkStatus.IN_OFFICE, commute2.getWorkStatus());
    }

    @Test
    @DisplayName("퇴근 또는 자리 비움을 기록한다.")
    void testMarkLeaveOrOutOffice() {
        // Given
        User user = mock(User.class);
        Commute existingCommute = new Commute(user, LocalDate.of(2024, 7, 23), LocalTime.of(9, 0), LocalTime.of(18, 0), AttendanceStatus.PRESENT, WorkStatus.IN_OFFICE);

        when(timeService.nowDate()).thenReturn(LocalDate.of(2024, 7, 23));
        when(timeService.nowDateTime()).thenReturn(LocalDateTime.of(2024, 7, 23, 18, 6));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(commuteRepository.findByUserAndDate(user, LocalDate.of(2024, 7, 23))).thenReturn(Optional.of(existingCommute));

        // When
        commuteService.markLeaveOrOutOffice();

        // Then
        verify(commuteRepository, times(1)).save(commuteCapture.capture());
        Commute updatedCommute = commuteCapture.getValue();

        assertEquals(user, updatedCommute.getUser());
        assertEquals(LocalDate.of(2024, 7, 23), updatedCommute.getDate());
        assertEquals(LocalTime.of(9, 0), updatedCommute.getStartedAt());
        assertEquals(LocalTime.of(18, 0), updatedCommute.getEndedAt());
        assertEquals(AttendanceStatus.PRESENT, updatedCommute.getAttendanceStatus());
        assertEquals(WorkStatus.OUT_OFF_OFFICE, updatedCommute.getWorkStatus());
    }
}