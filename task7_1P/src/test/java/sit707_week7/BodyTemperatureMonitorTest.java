package sit707_week7;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

public class BodyTemperatureMonitorTest {

    @Mock
    TemperatureSensor temperatureSensor;
    
    @Mock
    CloudService cloudService;
    
    @Mock
    NotificationSender notificationSender;
    
    BodyTemperatureMonitor bodyTemperatureMonitor;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bodyTemperatureMonitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
    }

    @Test
    public void testStudentIdentity() {
        String studentId = "220437608";  // Provide your student ID
        assertNotNull("Student ID is null", studentId);
    }

    @Test
    public void testStudentName() {
        String studentName = "Azmah";  // Provide your name
        assertNotNull("Student name is null", studentName);
    }

    @Test
    public void testReadTemperatureNegative() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(-10.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        assertEquals(-10.0, temperature, 0.0);
    }

    @Test
    public void testReadTemperatureZero() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(0.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        assertEquals(0.0, temperature, 0.0);
    }

    @Test
    public void testReadTemperatureNormal() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        double temperature = bodyTemperatureMonitor.readTemperature();
        assertEquals(36.5, temperature, 0.0);
    }

    @Test
    public void testReadTemperatureAbnormallyHigh() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(41.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        assertEquals(41.0, temperature, 0.0);
    }

    @Test
    public void testReportTemperatureReadingToCloud() {
        TemperatureReading temperatureReading = new TemperatureReading(36.5);
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(temperatureReading);
        verify(cloudService, times(1)).sendTemperatureToCloud(temperatureReading);
    }

    @Test
    public void testInquireBodyStatusNormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
    }

    @Test
    public void testInquireBodyStatusAbnormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
}
