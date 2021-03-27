package brisdalen.demo.services;

import brisdalen.demo.domain.BestillingsOrdre;
import brisdalen.demo.domain.Bok;
import brisdalen.demo.domain.MestSolgteLinje;
import brisdalen.demo.domain.OrdreLinje;
import brisdalen.demo.rest.dtos.BestillingDto;
import brisdalen.demo.rest.dtos.NyBokRegistreringDto;
import brisdalen.demo.rest.dtos.OppdaterBeholdningDto;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
// På grunn av tidsbegrensning bruker jeg kun controller- og service-layer
// I en ordentlig applikasjon ville jeg også brukt et Repository layer og en database, med f.eks. JPA for spørringer
public class OrdreService {

    private HashMap<Long, Bok> bøker;
    private HashMap<String, BestillingsOrdre> ordre;
    private HashMap<DayOfWeek, Integer> dagSolgt; // 1 = Mandag, 2 = Tirsdag, osv.

    public OrdreService() {
        this.bøker = new HashMap<>();
        this.ordre = new HashMap<>();
        this.dagSolgt = new HashMap<>();
        bøker.put(1234567L, new Bok(1234567, "God bok 1", 199, 500));
        bøker.put(1234568L, new Bok(1234568, "God bok 2", 299, 432));
        bøker.put(1234569L, new Bok(1234569, "God bok 3", 249, 235));
        bøker.put(1234577L, new Bok(1234577, "God bok 4", 149, 45));
        bøker.put(1234578L, new Bok(1234578, "God bok 5", 99, 89));
    }

    public List<Bok> getAll() {
        return bøker.values().stream()
                .sorted(Comparator.comparing(Bok::getISBN))
                .collect(Collectors.toList());
    }

    public boolean existsById(long id) {
        return bøker.containsKey(id);
    }

    public BestillingsOrdre bestill(BestillingDto dto) {
        var orderLines = new ArrayList<OrdreLinje>();
        String feilbeskjed = "";
        for(var bestilling : dto.getBøker()) {
            Bok b = bøker.get(bestilling.getISBN());
            if(b.getAntallPåLager() == 0) {
                addLineToFeilbeskjed(feilbeskjed, String.format("%s med ISBN %d er desverre utsolgt.", b.getTittel(), b.getISBN()));
            } else {
                int antallBestilt = (bestilling.getAntallØnsket() <= b.getAntallPåLager()) ? bestilling.getAntallØnsket() : b.getAntallPåLager();
                // trekke fra lagerbeholdning
                if(bestilling.getAntallØnsket() <= b.getAntallPåLager()) {
                    b.setAntallPåLager(b.getAntallPåLager() - bestilling.getAntallØnsket());
                } else {
                    b.setAntallPåLager(0);
                }
                OrdreLinje nyOrdreLinje = new OrdreLinje(b.getISBN(), b.getTittel(), b.getPris(), antallBestilt);
                orderLines.add(nyOrdreLinje);
                dagSolgt.put(DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)), nyOrdreLinje.getAntall());
            }
        }
        orderLines.forEach(b -> b.setLinjeTotal(b.getPris() * b.getAntall()));

        double sum = orderLines.stream()
                .mapToDouble(b -> b.getPris() * b.getAntall())
                .sum();
        return new BestillingsOrdre(newOrdreId(), orderLines, sum);
    }

    public List<MestSolgteLinje> mostSoldByWeekDay() {
        List<MestSolgteLinje> mestSolgteLinjer = new ArrayList<>();
        dagSolgt.forEach((key, value) -> mestSolgteLinjer.add(new MestSolgteLinje(key, value)));
        mestSolgteLinjer.sort(Comparator.comparing(MestSolgteLinje::getAntallSolgt).reversed());
        return mestSolgteLinjer;
    }

    public Bok registrerNyBok(NyBokRegistreringDto dto) {
        Bok ny = new Bok(dto.getISBN(), dto.getTittel(), dto.getPris(), dto.getAntall());
        bøker.put(dto.getISBN(), ny);
        return ny;
    }

    public void oppdaterBeholdning(@RequestBody OppdaterBeholdningDto dto) {
        for(var linje : dto.getOppdateringer()) {
            long ISBN = linje.getISBN();
            bøker.get(ISBN).setAntallPåLager(bøker.get(ISBN).getAntallPåLager() + linje.getAntallNye());
        }
    }

    private void addLineToFeilbeskjed(String original, String newLine) {
        original = String.format("%s \n%s", original, newLine);
    }

    private String newOrdreId() {
        return "o-" + (ordre.size() + 1);
    }
}
