package brisdalen.demo.rest.controllers;

import brisdalen.demo.rest.dtos.BestillingDto;
import brisdalen.demo.rest.dtos.NyBokRegistreringDto;
import brisdalen.demo.rest.dtos.OppdaterBeholdningDto;
import brisdalen.demo.rest.dtos.OppdaterBeholdningLinjeDto;
import brisdalen.demo.services.OrdreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bøker")
public class BokController {

    /*
    Lag en metode for å bestille bøker. Det skal være mulig å bestille flere titler på en gang.
Det skal også være mulig å bestille mer enn en av hver bok.
Det er positivt hvis metoden tilgjengeliggjøres som en rest-tjeneste.

Metoden skal gi beskjed dersom det ikke er flere bøker igjen på lager av en av
titlene, men hele bestillingen skal **ikke** kanselleres hvis det er utsolgt for 1 bok.

Responsen fra metoden skal være en oppsummering av hva som er bestilt og totalpris.
     */

    private final OrdreService ORDRE_SERVICE;

    @Autowired
    public BokController(OrdreService OrdreService) {
        this.ORDRE_SERVICE = OrdreService;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(path = "/")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ORDRE_SERVICE.getAll());
    }

    @PostMapping(path = "/bestill", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bestill(@RequestBody BestillingDto dto) {
        if(dto.getBøker().length <= 0) {
            return ResponseEntity.badRequest().body("Bestilling må inkludere noen bøker i orderen.");
        }
        return ResponseEntity.ok(ORDRE_SERVICE.bestill(dto));
    }

    @GetMapping(path = "/admin/mostsold/by/week-day")
    public ResponseEntity<?> mostSoldByWeekDay() {
        var result = ORDRE_SERVICE.mostSoldByWeekDay();
        String responseBody = "Dager med mest solgte bøker: \nBestselgende ukedag: ";

        for(var d : result) {
            responseBody = String.format(responseBody + "%s med %d solgte bøker", d.getDag(), d.getAntallSolgt());
        }

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping(path = "/admin/register/book")
    public ResponseEntity<?> registrerNyBok(@RequestBody NyBokRegistreringDto dto) {
        if(dto.getISBN() <= 0) {
            return ResponseEntity.badRequest().body("Ny registrering krever godkjent ISBN nummer.");
        }
        if(dto.getTittel().isBlank()) {
            return ResponseEntity.badRequest().body("Ny registrering krever en tittel.");
        }
        if(dto.getPris() <= 0) {
            return ResponseEntity.badRequest().body("Ny registrering krever et positivt tall for pris.");
        }
        if(dto.getAntall() < 0) {
            return ResponseEntity.badRequest().body("Ny registrering krever et positivt tall eller 0 for antall.");
        }
        return ResponseEntity.ok(ORDRE_SERVICE.registrerNyBok(dto));
    }

    @PostMapping(path = "/admin/oppdater/beholdning")
    public ResponseEntity<?> oppdaterBeholdning(@RequestBody OppdaterBeholdningDto dto) {
        if(dto.getOppdateringer().length == 0) {
            return ResponseEntity.badRequest().body("Oppdatering av beholdning et antall bøker å registrere.");
        }
        ORDRE_SERVICE.oppdaterBeholdning(dto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
