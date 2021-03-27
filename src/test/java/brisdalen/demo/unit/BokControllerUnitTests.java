package brisdalen.demo.unit;

import brisdalen.demo.domain.BestillingsOrdre;
import brisdalen.demo.domain.Bok;
import brisdalen.demo.domain.MestSolgteLinje;
import brisdalen.demo.domain.OrdreLinje;
import brisdalen.demo.rest.controllers.BokController;
import brisdalen.demo.rest.dtos.BestillingDto;
import brisdalen.demo.rest.dtos.BestillingEnkeltBokDto;
import brisdalen.demo.services.OrdreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@SpringBootTest // Med mer tid ville jeg satt opp tester med @WebMvcTest
public class BokControllerUnitTests {

    @Autowired
    private BokController controller;

    @MockBean
    private OrdreService ordreService;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void init() {

    }

    @Test
    void bestillValid_returnOK_andNoErrorMessage() {
        // Arrange
        var bestilling = new BestillingEnkeltBokDto[]{
                new BestillingEnkeltBokDto(1234567, 10),
                new BestillingEnkeltBokDto(1234568, 20)};
        BestillingDto dto = new BestillingDto("o-1", bestilling);

        // Act
        var response = controller.bestill(dto);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(response.toString().contains("desverre utsolgt"));
    }

    @Test
    void mestSolgteBok_returnOK_andTodayMostSold() {
        // Arrange
        var bestilling = new BestillingEnkeltBokDto[]{
                new BestillingEnkeltBokDto(1234567, 10),
                new BestillingEnkeltBokDto(1234568, 20)};
        BestillingDto dto = new BestillingDto("o-1", bestilling);

        List<OrdreLinje> ordreLinjer = new ArrayList<>(List.of(
                new OrdreLinje(bestilling[0].getISBN(), "God bok 1", 199, bestilling[0].getAntallØnsket()),
                new OrdreLinje(bestilling[1].getISBN(), "God bok 2", 299, bestilling[1].getAntallØnsket())
        ));
        Mockito.when(ordreService.bestill(dto)).thenReturn(new BestillingsOrdre(dto.getBestillingsId(), ordreLinjer, 7970.0));
        Mockito.when(ordreService.mostSoldByWeekDay()).thenReturn(new ArrayList<>(List.of(
                new MestSolgteLinje(DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)),
                        bestilling[0].getAntallØnsket() + bestilling[1].getAntallØnsket())
        )));

        // Act
        controller.bestill(dto);
        var response = controller.mostSoldByWeekDay();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.toString().contains(
                String.format("Bestselgende ukedag: %s", DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))
        ));
    }
}
