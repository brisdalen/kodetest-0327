package brisdalen.demo.domain;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class OrdreLinje {
    @NonNull
    private long ISBN;
    @NonNull
    private String tittel;
    @NonNull
    private double pris;
    @NonNull
    private int antall;

    private double linjeTotal;
}
