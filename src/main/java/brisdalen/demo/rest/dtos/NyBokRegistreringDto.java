package brisdalen.demo.rest.dtos;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class NyBokRegistreringDto {
    @NonNull
    private long ISBN;
    @NonNull
    private String tittel;
    @NonNull
    private double pris;
    @NonNull
    private int antall;
}
