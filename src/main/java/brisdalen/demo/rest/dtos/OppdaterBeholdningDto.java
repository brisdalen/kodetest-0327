package brisdalen.demo.rest.dtos;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class OppdaterBeholdningDto {
    @NonNull
    private OppdaterBeholdningLinjeDto[] oppdateringer;
}
