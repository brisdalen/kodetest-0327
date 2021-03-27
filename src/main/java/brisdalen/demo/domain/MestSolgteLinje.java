package brisdalen.demo.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

@RequiredArgsConstructor
@Getter
public class MestSolgteLinje {
    @NonNull
    private DayOfWeek dag;
    @NonNull
    private int antallSolgt;
}