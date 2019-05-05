package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class Logger {


    HashMap<String,String> phrases;
    FileWriter logsfile;


    Logger(String filename) throws IOException
    {
        phrases = new HashMap<>();
        phrases.put("lvs_start","ЗапускЛВС\r\n");
        phrases.put("lvs_restart","ПерезапускЛВС\r\n");
        phrases.put("status_working","Статус: ВсеОУработают\r\n");
        phrases.put("ou_turn_off","ОУ № искусственно выключен\r\n");
        phrases.put("ou_turn_on","ОУ № искусственно включен\r\n");
        phrases.put("ou_turn_failure","На ОУ № искусственно включен сбой\r\n");
        phrases.put("ou_turn_denial","На ОУ № искусственно включен отказ\r\n");
        phrases.put("ou_turn_generating","На ОУ № искусственно включена генерация\r\n");
        phrases.put("make_round","Производится обход\r\n");
        phrases.put("status_ou_off","Статус: ОУ № выключен\r\n");
        phrases.put("status_ou_failure","Статус: НаОУ № произошелсбой\r\n");
        phrases.put("status_ou_denial","Статус: НаОУ № произошелотказ\r\n");
        phrases.put("status_ou_generator","Статус: ОУ № генератор\r\n");
        phrases.put("makeround_fail","Невозможно выполнить обход на линии\r\n");
        phrases.put("active_line","Активная линия №\r\n");

        logsfile = new FileWriter(filename);
        Date date = new Date();
        addToFile(date.toString());
        addToFile("\r\n------------------------------\r\n");
    }

    public String getLogsLine(String type) throws IOException
    {
        String ntype = (!(phrases.get(type).equals(""))) ? phrases.get(type) : type;
        addToFile(ntype);
        return ntype;
    }

    public void addToFile(String logline) throws IOException
    {
        logsfile.write(logline);
    }

    public void closeFile()throws IOException{
        logsfile.close();
    }

}