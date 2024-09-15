package project.atch.domain.festival;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FestivalCache {

    private Map<LocalDate, List<FestivalDto>> cache = new HashMap<>();

    public List<FestivalDto> getFestivalData(LocalDate date) {
        return cache.get(date);
    }

    public void setFestivalData(LocalDate date, List<FestivalDto> data) {
        cache.put(date, data);
    }

    public boolean hasDataForToday(LocalDate date) {
        return cache.containsKey(date);
    }

}
