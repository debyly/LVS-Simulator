package model;

import java.util.HashMap;
import java.util.Iterator;

public class Message {

    String state;
    HashMap<String,Integer> commands;
    HashMap<String, Integer> modes;
    HashMap<Integer,String> IntStr;
    String type;
    int address_from;
    int address_to;

    Message(){
        commands = new HashMap<>();
        modes = new HashMap<>();
        IntStr = new HashMap<>();

        commands.put("give_response",1);
        commands.put("block",2);
        commands.put("unblock",3);

        modes.put("command", 31);
        modes.put("subaddr", 30);

        IntStr.put(0,"00000"); IntStr.put(1,"00001"); IntStr.put(2,"00010"); IntStr.put(3,"00011");
        IntStr.put(4,"00100"); IntStr.put(5,"00101"); IntStr.put(6,"00110"); IntStr.put(7,"00111");
        IntStr.put(8,"01000"); IntStr.put(9,"01001"); IntStr.put(10,"01010");IntStr.put(11,"01011");
        IntStr.put(12,"01100"); IntStr.put(13,"01101"); IntStr.put(14,"01110"); IntStr.put(15,"01111");
        IntStr.put(16,"10000"); IntStr.put(17,"10001"); IntStr.put(18,"10010");  IntStr.put(31, "11111");
        IntStr.put(30, "11110");
    }

    String encodeMessage(int address, int type, String mode_name, String command)
    {
        String message = "";
        String synchr = "111"; // Синхросигнал
        String addrTo = IntStr.get(address); // Адрес ОУ
        String K = "0"; // Разряд "Прием/Передача"
        String mode; // Подадрес или режим управления
        String wordCount; // Количество СД или КУ
        int lastbit = 0; // Бит четности
        switch (type){
            case 1:
                //Кодирование режима управления
                int nmode = modes.get(mode_name);
                mode = IntStr.get(nmode);

                //Кодирование КУ или числа СД
                if (nmode == 30) wordCount = IntStr.get(12);
                else{
                    int nCom = commands.get(command);
                    wordCount = IntStr.get(nCom);
                }

                message = synchr + addrTo + K + mode + wordCount;
                break;
            case 2:
                message = synchr + "0110101001010010";
                break;
            case 3:
                message = synchr + addrTo + "010000000000";
        }
        for(int i = 0; i < 19; i++){
            int bit = (message.charAt(i) == '1') ? 1 : 0;
            lastbit += bit;
        }
        message += String.valueOf(lastbit % 2);

        return message;
    }
}
