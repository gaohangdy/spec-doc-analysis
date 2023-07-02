/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.FOLDER_NAME_PAGE;
import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.FOLDER_NAME_PART;
import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.OUTPUT_PATH;
import com.uxhub.analysis.design.doc.models.Doc;
import com.uxhub.analysis.design.doc.models.Part;
import com.uxhub.analysis.design.doc.models.PartElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author gaohang
 */
public class ReadAllParts {

    public final static String TEMPATE_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\分析用テンプレート\\";
    private final static String TEMPLATE_NAME = "パーツ一覧_template.xlsx";
    private final static String OUTPUT_FILE_PREFIX = "パーツ一覧";

    public static void execute(String docRoot) {
        List<Doc> docList = new ArrayList<>();

        readDocs(docRoot + FOLDER_NAME_PAGE, docList, "画面");
        readDocs(docRoot + FOLDER_NAME_PART, docList, "パーツ");
        System.out.println("Found files: " + docList.size());

        List<Part> partList = readPartBacicInfoList(docList);
        try {
            writeToDocument(partList);
        } catch (IOException ex) {
            System.out.println("Write export file error.");
        }
        System.out.println("Found parts: " + partList.size());
    }

    private static List<Part> readPartBacicInfoList(List<Doc> docList) {
        List<Part> partList = new ArrayList<>();

        for (Doc doc : docList) {
            if (doc.getFullPath().indexOf("画面設計書（フロント）_MCCMG0050_注文完了画面.xlsx") > 0) {
                System.out.println("Read PartS: " + doc.getFullPath());
            }
            try ( FileInputStream filePart = new FileInputStream(new File(doc.getFullPath()))) {
                XSSFWorkbook workbook = new XSSFWorkbook(filePart);
                reaPartBasicInfo(doc, workbook, partList);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + doc.getFileName() + " | " + doc.getFullPath());
            } catch (IOException ex) {
                System.err.println("Can not read  : " + doc.getFileName() + " | " + doc.getFullPath());
            }
        }

        return partList;
    }

    private static void reaPartBasicInfo(Doc doc, XSSFWorkbook workbook, List<Part> partList) {
        String pattern = "(パーツ設計書\\()(.*?)(\\))";

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet currentSheet = workbook.getSheetAt(i);
            String sheetName = currentSheet.getSheetName().trim();

            Part part = null;
            boolean isMatch = Pattern.matches(pattern, sheetName);
            if (isMatch) {
                part = new Part();
                String partId = sheetName.replaceAll("(^パーツ設計書\\()+|(\\))", "");

                part.setPartId(partId);
                part.setPartName(fetchPartName(currentSheet.getRow(0)));
            } else {
                if ("パーツ設計書".equals(sheetName)) {
                    part = new Part();
                    part.setPartId(doc.getId());
                    part.setPartName(doc.getName());
                }
            }

            if (part != null) {
                part.setCommonPart("パーツ".equals(doc.getType()));
                part.setFileName(doc.getFileName());
                part.setFilePath(doc.getFullPath());
                part.setSheetName(sheetName);

                ReadUtils.fetchPartElements(currentSheet, part);
                System.out.println(currentSheet.getSheetName() + " " + part.getElements().size());
                partList.add(part);
            }
        }
    }

    private static void readDocs(String path, List<Doc> fileList, String docType) {
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory() && !"010.会員".equals(f.getName())) {
                    readDocs(f.getAbsolutePath(), fileList, docType);
                } else {
                    // 古いバージョンの設計書を対象外
                    if (!f.getName().startsWith("dummy_") && f.getName().endsWith(".xlsx")) {
                        Doc doc = formatDoc(f, docType);
                        if (doc != null) {
                            fileList.add(formatDoc(f, docType));
                        } else {
                            System.out.println("File format error: " + f.getName());
                        }
                    }
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }
    }

    private static Doc formatDoc(File file, String docType) {
        Doc doc = new Doc();
        String name = file.getName().replace(".xlsx", "");
        String[] arrayFileName = name.split("_");

        if (arrayFileName.length != 3) {
            return null;
        }

        doc.setId(arrayFileName[1]);
        doc.setName(arrayFileName[2]);
        doc.setFileName(file.getName());
        doc.setPath(file.getPath());
        doc.setFullPath(file.getAbsolutePath());
        doc.setType(docType);

        return doc;
    }

    private static String fetchPartName(Row row) {
        boolean matchPartNameLabel = false;
        for (int i = 0; i < 100; i++) {
            if (row.getCell(i) != null && row.getCell(i).getCellType() == CellType.STRING && "パーツ名".equals(row.getCell(i).getStringCellValue())) {
                matchPartNameLabel = true;
                continue;
            }
            if (matchPartNameLabel && row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
                return row.getCell(i).getStringCellValue().trim();
            }
        }
        return "Cannot find Part Name";
    }

    private static void writeToDocument(List<Part> partList) throws IOException {

        partList.sort(Comparator.comparing(Part::getPartId));

        FileInputStream inputStream = new FileInputStream(new File(TEMPATE_PATH + TEMPLATE_NAME));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheetPage = workbook.getSheet("リスト");

            writeDatas(sheetPage, partList);
            
            Sheet sheetElements = workbook.getSheet("パーツ項目一覧");
            writeElementDatas(sheetElements, partList);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(OUTPUT_PATH + "\\export\\" + OUTPUT_FILE_PREFIX + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writeDatas(Sheet sheet, List<Part> partList) {
        int startRowNum = 1;
        for (Part part : partList) {
//            Row row = sheet.createRow(++startRowNum);
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
            row.getCell(2).setCellValue(part.getPartId());
            // 画面名
            if (row.getCell(3) == null) {
                row.createCell(3);
            }
            row.getCell(3).setCellValue(part.getPartName());
            if (row.getCell(4) == null) {
                row.createCell(4);
            }
            row.getCell(4).setCellValue(part.isCommonPart() ? "〇" : "-");
            // パラメータ
            if (row.getCell(5) == null) {
                row.createCell(5);
            }
            row.getCell(5).setCellValue(part.getFileName());
            if (row.getCell(6) == null) {
                row.createCell(6);
            }
            row.getCell(6).setCellValue(part.getSheetName());
            // パーツ
            if (row.getCell(7) == null) {
                row.createCell(7);
            }
            row.getCell(7).setCellValue(part.getFilePath());
        }
    }

    private static void writeElementDatas(Sheet sheet, List<Part> partList) {
        int startRowNum = 1;
        for (Part part : partList) {
            for (PartElement element : part.getElements()) {
                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }
                // No.
                if (row.getCell(1) == null) {
                    row.createCell(1);
                }
                row.getCell(1).setCellValue(startRowNum - 1);
                
                if (row.getCell(2) == null) {
                    row.createCell(2);
                }
                row.getCell(2).setCellValue(part.getPartId());   
                
                if (row.getCell(3) == null) {
                    row.createCell(3);
                }
                row.getCell(3).setCellValue(part.getPartName());    
                
                if (row.getCell(4) == null) {
                    row.createCell(4);
                }
                row.getCell(4).setCellValue(element.getName());       
                
                if (row.getCell(5) == null) {
                    row.createCell(5);
                }
                row.getCell(5).setCellValue(element.getId());    
                
                if (row.getCell(6) == null) {
                    row.createCell(6);
                }
                row.getCell(6).setCellValue(element.getType());         
                
                if (row.getCell(7) == null) {
                    row.createCell(7);
                }
                row.getCell(7).setCellValue(element.getIoType());    
                
                if (row.getCell(8) == null) {
                    row.createCell(8);
                }
                row.getCell(8).setCellValue(element.getEvent());  
                
                if (row.getCell(9) == null) {
                    row.createCell(9);
                }
                row.getCell(9).setCellValue(element.getDescription());  
                
                if (row.getCell(10) == null) {
                    row.createCell(10);
                }
                row.getCell(10).setCellValue(element.getAdditional());                     
            }
        }
    }
}
