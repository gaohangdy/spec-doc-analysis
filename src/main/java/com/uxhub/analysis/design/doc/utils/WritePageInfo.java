/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.OUTPUT_PATH;
import com.uxhub.analysis.design.doc.models.Page;
import com.uxhub.analysis.design.doc.models.Part;
import com.uxhub.analysis.design.doc.models.PartDetail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gaohang
 */
public class WritePageInfo {

    public static final String TEMPLATE_NAME = "フォント開発タスクアナリシス_template.xlsx";
    public static final String TEMPLATE_LIST_NAME = "Page-Elements_template.xlsx";
    public static final String OUTPUT_NAME = "フォント開発タスクアナリシス_";
    public static final String OUTPUT_LIST_NAME = "Page-Elements_";

    public static void writeAnalysisData(List<Page> pageList, List<Part> partList, String outputFilePath) throws FileNotFoundException, IOException {
        outputFilePath = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\";

        FileInputStream inputStream = new FileInputStream(new File(outputFilePath + TEMPLATE_NAME));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheetPage = workbook.getSheet("01.画面組込");
            Sheet sheetPart = workbook.getSheet("02.パーツ");

            writePageSheet(sheetPage, pageList);
            writePartSheet(sheetPart, partList);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(outputFilePath + "\\export\\" + OUTPUT_NAME + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writePageSheet(Sheet sheet, List<Page> pageList) {
        int startRowNum = 2;
        for (Page page : pageList) {
//            Row row = sheet.createRow(++startRowNum);
            Row row = sheet.getRow(++startRowNum);
            if (row == null) {
                row = sheet.createRow(startRowNum);
            }
            
            // No.
            row.getCell(1).setCellValue(startRowNum - 2);
            // 画面ID
            row.getCell(2).setCellValue(page.getPageId());
            // 画面名
            row.getCell(3).setCellValue(page.getPageName());
            // 初期処理
            row.getCell(4).setCellValue(page.getMethodCnt());
            // パラメータ
            row.getCell(5).setCellValue(page.getParamterCnt());
            // パーツ
            row.getCell(6).setCellValue(page.getPartsCnt());
            // ページ要素
            row.getCell(7).setCellValue(page.getElementCnt());

        }

    }

    private static void writePartSheet(Sheet sheet, List<Part> partList) {
        int startRowNum = 2;
        for (Part part : partList) {
            if ("".equals(part.getPartId())) {
                System.out.println("Stop");
            }
//            Row row = sheet.createRow(++startRowNum);
            Row row = sheet.getRow(++startRowNum);
            if (row == null) {
                row = sheet.createRow(startRowNum);
            }
            row.getCell(1).setCellValue(startRowNum - 2);
            row.getCell(2).setCellValue(part.getPartId());
            row.getCell(3).setCellValue(part.getPartName());
            row.getCell(4).setCellValue(part.getUpdateType());
            row.getCell(5).setCellValue(part.getOutputCnt());
            row.getCell(6).setCellValue(part.getInputCnt());
            row.getCell(7).setCellValue(part.getStaticCnt());
            row.getCell(8).setCellValue(part.getEventCnt());
            row.getCell(9).setCellValue(part.getValidationCnt());
            row.getCell(10).setCellValue(part.getInitMethodCnt());

            row.getCell(11).setCellValue(part.isCommonPart() ? "〇" : "-");
        }
    }

    public static void writePagePartElement(List<PartDetail> partDetailList, List<PartDetail> elementDetailList, String outputFilePath) throws FileNotFoundException, IOException {
        outputFilePath = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\";

        FileInputStream inputStream = new FileInputStream(new File(outputFilePath + TEMPLATE_LIST_NAME));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheetPart = workbook.getSheet("01.パーツ一覧");
            Sheet sheetElement = workbook.getSheet("02.ページ要素一覧");

            writeListDatas(partDetailList, sheetPart);
            writeListDatas(elementDetailList, sheetElement);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(outputFilePath + "\\export\\" + OUTPUT_LIST_NAME + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writeListDatas(List<PartDetail> listData, Sheet sheet) {
        int startRowNum = 1;
        for (PartDetail part : listData) {
//            Row row = sheet.createRow(++startRowNum);
            Row row = sheet.getRow(++startRowNum);
            if (row == null) {
                row = sheet.createRow(startRowNum);
            }
            row.getCell(1).setCellValue(startRowNum - 1);
            row.getCell(2).setCellValue(part.getParentPage().getPageId());
            row.getCell(3).setCellValue(part.getParentPage().getPageName());
            row.getCell(4).setCellValue(part.getPartId());
            row.getCell(5).setCellValue(part.getPartName());
            row.getCell(6).setCellValue(part.getUpdateType());
            row.getCell(7).setCellValue(part.getEventDescription());

        }
    }

    /**
     * 画面要素一覧のパーツとページ要素の項目情報をエクスポート
     *
     * @param pageList
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writePageElementsList(List<Page> pageList) throws FileNotFoundException, IOException {
        String templatePath = ReadAllParts.TEMPATE_PATH + "画面要素一覧_画面別_template.xlsx";

        FileInputStream inputStream = new FileInputStream(new File(templatePath));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheetPage = workbook.getSheet("画面要素一覧");

            printPageElementsListData(sheetPage, pageList);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(OUTPUT_PATH + "\\export\\" + "画面要素一覧_画面別" + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void printPageElementsListData(Sheet sheet, List<Page> pageList) {
        int startRowNum = 1;
        for (Page page : pageList) {
            for (Part part : page.getPartList()) {
                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

                // No.
                if (row.getCell(1) == null) {
                    row.createCell(1);
                }
                row.getCell(1).setCellValue(startRowNum - 1);

                // 画面ID
                if (row.getCell(2) == null) {
                    row.createCell(2);
                }
                row.getCell(2).setCellValue(page.getPageId());
                // 画面名
                if (row.getCell(3) == null) {
                    row.createCell(3);
                }
                row.getCell(3).setCellValue(page.getPageName());

                if (row.getCell(4) == null) {
                    row.createCell(4);
                }
                row.getCell(4).setCellValue(part.getPartId());

                if (row.getCell(5) == null) {
                    row.createCell(5);
                }
                row.getCell(5).setCellValue(part.getPartName());

                if (row.getCell(6) == null) {
                    row.createCell(6);
                }
                row.getCell(6).setCellValue("パーツ");

                if (row.getCell(7) == null) {
                    row.createCell(7);
                }
                row.getCell(7).setCellValue(part.getOutputCnt());

                if (row.getCell(8) == null) {
                    row.createCell(8);
                }
                row.getCell(8).setCellValue(part.getInputCnt());

                if (row.getCell(9) == null) {
                    row.createCell(9);
                }
                row.getCell(9).setCellValue(part.getStaticCnt());

                if (row.getCell(10) == null) {
                    row.createCell(10);
                }
                row.getCell(10).setCellValue(part.getEventCnt());

                if (row.getCell(11) == null) {
                    row.createCell(11);
                }
                row.getCell(11).setCellValue(part.getValidationCnt());

                if (row.getCell(12) == null) {
                    row.createCell(12);
                }
                row.getCell(12).setCellValue(part.getInitMethodCnt());
                
                if ("P-0001".equals(part.getPartId()) || "P-0002".equals(part.getPartId()) || "P-0003".equals(part.getPartId()) ||
                        "MCACP0010".equals(part.getPartId()) || "MCACP0020".equals(part.getPartId()) || "MCACP0030".equals(part.getPartId())) {
                    if (row.getCell(13) == null) {
                        row.createCell(13);
                    }
                    row.getCell(13).setCellValue("●");                    
                }
                
            }

            for (PartDetail element : page.getElementList()) {
                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

                // No.
                if (row.getCell(1) == null) {
                    row.createCell(1);
                }
                row.getCell(1).setCellValue(startRowNum - 1);

                // 画面ID
                if (row.getCell(2) == null) {
                    row.createCell(2);
                }
                row.getCell(2).setCellValue(page.getPageId());
                // 画面名
                if (row.getCell(3) == null) {
                    row.createCell(3);
                }
                row.getCell(3).setCellValue(page.getPageName());

                if (row.getCell(4) == null) {
                    row.createCell(4);
                }
                row.getCell(4).setCellValue("-");

                if (row.getCell(5) == null) {
                    row.createCell(5);
                }
                row.getCell(5).setCellValue(element.getPartName());

                if (row.getCell(6) == null) {
                    row.createCell(6);
                }
                row.getCell(6).setCellValue("ページ要素");

                if (row.getCell(7) == null) {
                    row.createCell(7);
                }
                row.getCell(7).setCellValue(0);

                if (row.getCell(8) == null) {
                    row.createCell(8);
                }
                row.getCell(8).setCellValue(0);

                if (row.getCell(9) == null) {
                    row.createCell(9);
                }
                row.getCell(9).setCellValue(0);

                if (row.getCell(10) == null) {
                    row.createCell(10);
                }
                row.getCell(10).setCellValue(0);

                if (row.getCell(11) == null) {
                    row.createCell(11);
                }
                row.getCell(11).setCellValue(0);

                if (row.getCell(12) == null) {
                    row.createCell(12);
                }
                row.getCell(12).setCellValue(0);
            }
        }

    }
}
