package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> timeEntrys = new HashMap<>();

    private Long id = 1L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntrys.put(id, timeEntry);
        timeEntry.setId(id);
        id++;
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return timeEntrys.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntrys.values());
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        if (timeEntrys.containsKey(id)) {
            timeEntrys.put(id, timeEntry);
            return timeEntrys.get(id);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        timeEntrys.remove(id);
    }
}
