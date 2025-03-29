package com.example.spring_template.util.convert;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

public class CSVUtil {

//    Example usage:
//    try {
//        CsvUtil.processExcelFile(file, row -> {
//
//            try {
//                Customer newEntity  = CustomerCsvMapper.map(row);
//                this.createCustomer(newEntity);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    } catch (Exception e) {
//        throw new RuntimeException(e);
//    }

    public static void processExcelFile(MultipartFile file, Consumer<Row> rowProcessor) throws Exception {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);  // Read the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header row if needed
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                rowProcessor.accept(row);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception();
        }
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        int cellCount = row.getLastCellNum();
        for (int i = 0; i < cellCount; i++) {
            if (row.getCell(i) != null && !row.getCell(i).toString().trim().isEmpty()) {
                return false; // Row has at least one non-empty cell
            }
        }
        return true;
    }
}
