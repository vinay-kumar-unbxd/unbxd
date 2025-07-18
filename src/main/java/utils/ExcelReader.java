package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;


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
}
