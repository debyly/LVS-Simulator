import java.util.HashMap;

public class TimeCounter {


    int total_time;
    HashMap<String,Integer> time_types;

    TimeCounter()
    {
        time_types = new HashMap<>();
        total_time = 0;
        time_types.put("pause_if_bizy", 5000);
        time_types.put("command", 20);
        time_types.put("pause_before_answer", 12);
        time_types.put("word", 12*20);
        time_types.put("block", 20);
        time_types.put("unblock", 20);
        time_types.put("pause_between_messages", 1000);
        time_types.put("max_word_length", 800);
        time_types.put("answer", 20);
    }

    public void addTime(String type)
    {
        total_time += time_types.get(type);
    }

    public int getTime()
    {
        return total_time;
    }
}
