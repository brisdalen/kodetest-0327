package brisdalen.demo.rest.dtos;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BestillingEnkeltBokDto {
    @NonNull
    private long ISBN;
    @NonNull
    private int antallØnsket;
}
