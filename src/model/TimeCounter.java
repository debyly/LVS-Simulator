package model;

import java.util.HashMap;

import static model.TimeCounter.TimeType.*;

class TimeCounter {

    private int total_time = 0;
    private HashMap<TimeType,Integer> timeMap = new HashMap<>();

    public enum TimeType {WORD, COMMAND, ANSWER, BLOCK, UNBLOCK,
        PAUSE_IF_BUSY, PAUSE_BEFORE_ANSWER, PAUSE_BETWEEN_MESSAGES}

    TimeCounter()
    {
        timeMap.put(PAUSE_IF_BUSY, 5000);
        timeMap.put(COMMAND, 20);
        timeMap.put(PAUSE_BEFORE_ANSWER, 12);
        timeMap.put(WORD, 12*20);
        timeMap.put(BLOCK, 20);
        timeMap.put(UNBLOCK, 20);
        timeMap.put(PAUSE_BETWEEN_MESSAGES, 1000);
        timeMap.put(ANSWER, 20);
    }

    void addTime(TimeType type) {
        total_time += timeMap.get(type);
    }

    int getTime() {
        return total_time;
    }
}
