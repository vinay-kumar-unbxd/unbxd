package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;


public class ExcelReader {

    public static String[] readRowByKey(String fileName, String key) {
        try {
            InputStream inputStream = ExcelReader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fileName);
            }

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell firstCell = row.getCell(0);
                if (firstCell != null && firstCell.getCellType() == CellType.STRING &&
                        firstCell.getStringCellValue().trim().equalsIgnoreCase(key)) {

                    int totalCells = row.getLastCellNum();
                    String[] rowData = new String[totalCells];

                    for (int i = 0; i < totalCells; i++) {
                        Cell cell = row.getCell(i);
                        rowData[i] = (cell != null) ? cell.toString().trim() : "";
                    }

                    workbook.close();
                    return rowData;
                }
            }

            workbook.close();
            throw new RuntimeException("Key not found in Excel: " + key);

        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel: " + e.getMessage(), e);
        }
    }

    public static Map<String, String> readRowByKeyAsMap(String fileName, String key) {
        try (InputStream inputStream = ExcelReader.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = inputStream != null ? new XSSFWorkbook(inputStream) : null) {
            if (inputStream == null || workbook == null) {
                throw new RuntimeException("File not found: " + fileName);
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header
                Cell firstCell = row.getCell(0);
                if (firstCell != null && firstCell.getCellType() == CellType.STRING &&
                    firstCell.getStringCellValue().trim().equalsIgnoreCase(key)) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                        Cell headerCell = headerRow.getCell(i);
                        Cell valueCell = row.getCell(i);
                        String header = headerCell != null ? headerCell.toString().trim() : "Column" + i;
                        String value = valueCell != null ? valueCell.toString().trim() : "";
                        rowData.put(header, value);
                    }
                    return rowData;
                }
            }
            throw new RuntimeException("Key not found in Excel: " + key);
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel: " + e.getMessage(), e);
        }
    }
}
