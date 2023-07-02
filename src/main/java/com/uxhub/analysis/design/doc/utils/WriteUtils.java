/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import com.uxhub.analysis.design.doc.models.PacElement;
import com.uxhub.analysis.design.doc.models.PacPage;
import com.uxhub.analysis.design.doc.models.PacPart;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author gaohang
 */
public class WriteUtils {

    public static void writeSheetDatas(Sheet sheet, List<PacPage> pages, boolean isIncludeElements) {
        int startRowNum = 1;

        for (PacPage page : pages) {
            if (page.getPageId() == null || "".equals(page.getPageId())) {
                continue;
            }

            for (PacPart part : page.getParts()) {
                boolean isPartStart = true;
                if ("MCKNP0180".equals(part.getPartId())) {
                    System.out.println("Stop MCKNP0180");
                }
                startRowNum = writePartRow(page, part, isPartStart, true, sheet, startRowNum);
            }

            if (isIncludeElements) {
                for (PacElement element : page.getElements()) {
                    Row row = sheet.getRow(++startRowNum);
                    if (row == null) {
                        row = sheet.createRow(startRowNum);
                    }
                    writeElementRow(page, element, false, row, startRowNum);
                }
            }

            Row row = sheet.getRow(++startRowNum);
            if (row == null) {
                row = sheet.createRow(startRowNum);
            }

            writePageRow(page, row, startRowNum);
//            for (int intCol = 0; intCol < 10; intCol++) {
//                CellStyle cs = row.getCell(intCol + 1).getCellStyle();
//                cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//                cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                row.getCell(intCol + 1).setCellStyle(cs);
//            }
        }
    }

    private static int writePartRow(PacPage page, PacPart part, boolean isPartStart, boolean isPart, Sheet sheet, int startRowNum) {
        int intStartCol = 1;
        if (part.getApis().isEmpty()) {
            Row row = sheet.getRow(++startRowNum);
            if (row == null) {
                row = sheet.createRow(startRowNum);
            }

            intStartCol = writePageCols(row, page, intStartCol, startRowNum);
            intStartCol = writePartCols(row, part.getPartId(), part.getPartName(), isPart, intStartCol, false);

            intStartCol++;
            // API
            if (row.getCell(intStartCol) == null) {
                row.createCell(intStartCol);
            }
            row.getCell(intStartCol).setCellValue("-");

            intStartCol++;
            if (part.getErrors().isEmpty()) {
                intStartCol++;
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue("-");
            } else {
                writeErrorCol(sheet.getWorkbook(), row, intStartCol, part.getErrors());
            }
        } else {
            for (String api : part.getApis()) {
                intStartCol = 1;

                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

                intStartCol = writePageCols(row, page, intStartCol, startRowNum);
                intStartCol = writePartCols(row, part.getPartId(), part.getPartName(), isPart, intStartCol, false);

                intStartCol++;
                // API
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(api);

                if (isPartStart) {
                    intStartCol++;
                    if (part.getErrors().isEmpty()) {
                        intStartCol++;
                        if (row.getCell(intStartCol) == null) {
                            row.createCell(intStartCol);
                        }
                        row.getCell(intStartCol).setCellValue("-");
                    } else {
                        writeErrorCol(sheet.getWorkbook(), row, intStartCol, part.getErrors());
                    }
                }
            }
        }

        return startRowNum;
    }

    private static void writeElementRow(PacPage page, PacElement part, boolean isPart, Row row, int startRowNum) {
        int intStartCol = 1;

        intStartCol = writePageCols(row, page, intStartCol, startRowNum);
        intStartCol = writePartCols(row, part.getElementId(), part.getElementNo() + part.getDispCondition(), isPart, intStartCol, false);

        intStartCol++;
        // API
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue("-");

        intStartCol++;
        intStartCol++;
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue("-");
    }

    private static int writePageCols(Row row, PacPage page, int intStartCol, int startRowNum) {
        // No.
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(startRowNum - 1);

        intStartCol++;
        // Sub System
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(page.getScName());

        intStartCol++;
        // 画面ID
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(page.getPageId());

        intStartCol++;
        // 画面名
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(page.getPageName());

        return intStartCol;
    }

    private static int writePartCols(Row row, String id, String name, boolean isPart, int intStartCol, boolean isPageLine) {

        intStartCol++;
        // -- 画面要素ID
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(id);

        intStartCol++;
        // --画面要素名
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(name);

        intStartCol++;
        // --要素区分
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(isPageLine ? "-" : isPart ? "パーツ" : "ページ要素");

        return intStartCol;
    }

    private static void writeErrorCol(Workbook wb, Row row, int intStartCol, List<String> errors) {
        String message = errors.stream().collect(Collectors.joining("\r\n"));

        intStartCol++;
        // 画面ID
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }
        row.getCell(intStartCol).setCellValue(message);

        CellStyle cs = row.getCell(intStartCol).getCellStyle();
        cs.setWrapText(true);
        row.getCell(intStartCol).setCellStyle(cs);
    }

    private static void writePageRow(PacPage page, Row row, int startRowNum) {
        int intStartCol = 1;

        intStartCol = writePageCols(row, page, intStartCol, startRowNum);
        intStartCol = writePartCols(row, "-", "パーツ組込", false, intStartCol, true);

        intStartCol++;
        // API
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }

        if ("トップ画面".equals(page.getPageName())) {
            System.out.println("Stp");
        }
        
        List<String> apiList = page.getApis().stream().distinct().collect(Collectors.toList());
        String apis = apiList.isEmpty() ? "-" : apiList.stream().collect(Collectors.joining(", "));
        apis = apis.replace("・　", "");
        apis = apis.replace("・", "");        
        row.getCell(intStartCol).setCellValue(apis);

        intStartCol++;
        intStartCol++;
        if (row.getCell(intStartCol) == null) {
            row.createCell(intStartCol);
        }

        String errors = page.getErrors().isEmpty() ? "-" : page.getErrors().stream().collect(Collectors.joining("\r\n"));
        row.getCell(intStartCol).setCellValue(errors);
    }
}
