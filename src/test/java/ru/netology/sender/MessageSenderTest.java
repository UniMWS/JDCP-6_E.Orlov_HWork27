package ru.netology.sender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.i18n.LocalizationService;

import java.util.HashMap;
import java.util.Map;


class MessageSenderTest {
    private GeoService geoService;
    private LocalizationService localizationService;
    private MessageSender messageSender;
    private Map<String, String> headers;

    @BeforeEach
    void setUp() {
        geoService = Mockito.mock(GeoService.class);
        localizationService = Mockito.mock(LocalizationService.class);
        messageSender = new MessageSenderImpl(geoService, localizationService);
        headers = new HashMap<>();
    }

    @Test
    public void testSendMessageRussia() {
        Mockito.when(geoService.byIp("172."))
                .thenReturn(new Location(null, Country.RUSSIA, null, 0));
        Mockito.when(localizationService.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.");
        String actual = messageSender.send(headers);
        String expected = localizationService.locale(Country.RUSSIA);

        Mockito.verify(geoService, Mockito.times(1)).byIp("172.");
        Mockito.verify(geoService, Mockito.never()).byIp("96.");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testSendMessageUSA() {
        Mockito.when(geoService.byIp("96.44.183.149"))
                .thenReturn(new Location("New York", Country.USA, " 10th Avenue", 32));
        Mockito.when(localizationService.locale(Country.USA))
                .thenReturn("Welcome");
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");
        String actual = messageSender.send(headers);
        String expected = localizationService.locale(Country.USA);

        Mockito.verify(geoService, Mockito.times(1)).byIp("96.44.183.149");
        Mockito.verify(geoService, Mockito.never()).byIp("172.");
        Assertions.assertEquals(expected, actual);
    }
}