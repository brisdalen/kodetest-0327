package brisdalen.demo.rest.dtos;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BestillingDto { // I en ordentlig applikasjon ville jeg ha spesifisert @Entity og @Column hvis den er knytta til en database
    @NonNull
    private String bestillingsId;
    @NonNull
    private BestillingEnkeltBokDto[] bøker;
}
