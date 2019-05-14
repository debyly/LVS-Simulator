package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ExcelBook {

    private final XSSFWorkbook workbook = new XSSFWorkbook();

    private XSSFCellStyle createStyleForTitle() {

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle createRegularStyle() {

        XSSFFont font = workbook.createFont();
        font.setBold(false);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font);
        return style;
    }


    void addSheet(String sheetName, List<List<Double>> table, ArrayList<String> columns){

        XSSFSheet sheet = workbook.createSheet(sheetName);
        Cell cell;
        Row row = sheet.createRow(0);
        XSSFCellStyle styleForTitle = createStyleForTitle();
        XSSFCellStyle styleRegular = createRegularStyle();

        for (int i = 0; i < columns.size(); i ++){

            cell = row.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(styleForTitle);
        }
        for (int i = 0; i < table.size(); i ++){

            row = sheet.createRow(i + 1);
            for (int j = 0; j < table.get(i).size(); j ++){

                cell = row.createCell(j);
                cell.setCellValue(table.get(i).get(j));
                cell.setCellStyle(styleRegular);
            }
        }
        for (int i = 0; i < columns.size(); i ++)
            sheet.autoSizeColumn(i);
    }

    void addToSheet(int fromLine, String sheetName, List<List<Double>> table, ArrayList<String> columns){

        XSSFSheet sheet = workbook.getSheet(sheetName);
        Cell cell;
        Row row = sheet.createRow(fromLine);
        XSSFCellStyle styleForTitle = createStyleForTitle();
        XSSFCellStyle styleRegular = createRegularStyle();

        for (int i = 0; i < columns.size(); i ++){

            cell = row.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(styleForTitle);
        }
        for (int i = 0; i < table.size(); i ++){

            row = sheet.createRow(i + 1 + fromLine);
            for (int j = 0; j < table.get(i).size(); j ++){

                cell = row.createCell(j);
                cell.setCellValue(table.get(i).get(j));
                cell.setCellStyle(styleRegular);
            }
        }
        for (int i = 0; i < columns.size(); i ++)
            sheet.autoSizeColumn(i);

    }

    void SaveXLS(File file)
            throws NullPointerException, IOException {

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        outFile.close();
    }
}
