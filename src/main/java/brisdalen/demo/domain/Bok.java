package brisdalen.demo.domain;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Bok {
    @NonNull
    private long ISBN;
    @NonNull
    private String tittel;
    @NonNull
    private double pris;
    @NonNull
    private int antallPåLager;
}
