package brisdalen.demo.domain;

import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BestillingsOrdre {
    @NonNull
    private String ordreId;
    @NonNull
    private List<OrdreLinje> ordreLinjer;
    @NonNull
    private double totalPris;

    private String feilbeskjed;

    public void addOrdreLinje(OrdreLinje linje) {
        ordreLinjer.add(linje);
    }
}
