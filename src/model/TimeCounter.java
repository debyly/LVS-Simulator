package model;

import java.util.HashMap;

public class TimeCounter {

    private int total_time = 0;
    private HashMap<TimeType,Integer> time_types = new HashMap<>();
    public enum TimeType {WORD, COMMAND, ANSWER, BLOCK, UNBLOCK,
        PAUSE_IF_BUSY, PAUSE_BEFORE_ANSWER, PAUSE_BETWEEN_MESSAGES}

    TimeCounter()
    {
        time_types.put(TimeType.PAUSE_IF_BUSY, 5000);
        time_types.put(TimeType.COMMAND, 20);
        time_types.put(TimeType.PAUSE_BEFORE_ANSWER, 12);
        time_types.put(TimeType.WORD, 12*20);
        time_types.put(TimeType.BLOCK, 20);
        time_types.put(TimeType.UNBLOCK, 20);
        time_types.put(TimeType.PAUSE_BETWEEN_MESSAGES, 1000);
        time_types.put(TimeType.ANSWER, 20);
    }

    void addTime(TimeType type) {
        total_time += time_types.get(type);
    }

    int getTime() {
        return total_time;
    }
}
