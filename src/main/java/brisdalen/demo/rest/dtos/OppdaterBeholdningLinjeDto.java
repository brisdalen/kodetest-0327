package brisdalen.demo.rest.dtos;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OppdaterBeholdningLinjeDto {
    @NonNull
    private long ISBN;
    @NonNull
    private int antallNye;
}
