package model;

import java.util.HashMap;

import static model.TimeCounter.TimeType.*;

class TimeCounter {

    private int total_time = 0;

    public enum TimeType {WORD, COMMAND, ANSWER, BLOCK, UNBLOCK,
        PAUSE_IF_BUSY, PAUSE_BEFORE_ANSWER, PAUSE_BETWEEN_MESSAGES}

    private final HashMap<TimeType,Integer> timeMap = new HashMap<TimeType,Integer>(){{

        put(PAUSE_IF_BUSY, 5000);
        put(COMMAND, 20);
        put(PAUSE_BEFORE_ANSWER, 12);
        put(WORD, 12*20);
        put(BLOCK, 20);
        put(UNBLOCK, 20);
        put(PAUSE_BETWEEN_MESSAGES, 1000);
        put(ANSWER, 20);
    }};

    void addTime(TimeType type) {
        total_time += timeMap.get(type);
    }

    int getTime() {
        return total_time;
    }
}