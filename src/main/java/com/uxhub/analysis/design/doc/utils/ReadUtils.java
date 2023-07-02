/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import com.opencsv.CSVReader;
import com.uxhub.analysis.design.doc.models.CommonLogic;
import com.uxhub.analysis.design.doc.models.Message;
import com.uxhub.analysis.design.doc.models.PacPart;
import com.uxhub.analysis.design.doc.models.Part;
import com.uxhub.analysis.design.doc.models.PartElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author gaohang
 */
public class ReadUtils {

    private final static String OUTPUT_PART_FILE_PREFIX = "【MKC】パーツ詳細設計書_";
    private final static String[] EXCLUDE_FOLDER_NAMES = {"廃止", "old"};
    private static List<CommonLogic> commonLogicListFileDatas;

    public static XSSFWorkbook readFile(String path) {
        try ( FileInputStream filePart = new FileInputStream(new File(path))) {
            XSSFWorkbook workbook = new XSSFWorkbook(filePart);
            return workbook;
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + path);
        } catch (IOException ex) {
            System.err.println("Can not read  : " + path);
        }
        return null;
    }

    public static int findCellIndex(Row row, int startPos) {
        int cellIndex = startPos;
        while (row.getCell(cellIndex) != null || row.getCell(cellIndex + 1) != null) {
            if (row.getCell(cellIndex) != null && row.getCell(cellIndex).getCellType() != CellType.BLANK) {
                return cellIndex;
            }
            cellIndex++;
        }
        return 0;
    }

    public static String getSubSystemName(String prefixId) {
        if (prefixId.length() > 4) {
            prefixId = prefixId.substring(0, 4);
        }
        switch (prefixId) {
            case "MCSH":
                return "商品";
            case "MCKN":
                return "会員";
            case "MCCM":
                return "注文";
            case "MCHS":
                return "販促";
            case "MCAC":
                return "アプリ共通";
            default:
                return null;
        }
    }

    public static void fetchPartElements(XSSFSheet sheet, Part part) {
        int[] colsIndex = {0, 2, 10, 17, 20, 22, 24, 26, 28, 30, 32, 34, 39, 49, 59};
        boolean isMatchElementTitle = false;
        boolean isMatchElementGridHeader = false;
        List<PartElement> elements = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getCell(0) != null
                    && row.getCell(0).getCellType() != CellType.BLANK
                    && row.getCell(0).getCellType() == CellType.STRING
                    && "項目説明一覧".equals(row.getCell(0).getStringCellValue())) {
                isMatchElementTitle = true;
            } else if (isMatchElementTitle) {
                if (row.getCell(0) != null
                        && row.getCell(0).getCellType() != CellType.BLANK
                        && row.getCell(0).getCellType() == CellType.STRING
                        && "NO".equals(row.getCell(0).getStringCellValue().toUpperCase())) {
                    isMatchElementGridHeader = true;
                } else if (isMatchElementGridHeader) {
                    if (getCellValue(row, colsIndex[0]) != null) {
                        PartElement element = new PartElement();
                        element.setName(getCellValue(row, colsIndex[1]));
                        element.setId(getCellValue(row, colsIndex[2]));
                        element.setType(getCellValue(row, colsIndex[3]));
                        element.setIoType(getCellValue(row, colsIndex[4]));
                        element.setEvent(getCellValue(row, colsIndex[12]));
                        element.setDescription(getCellValue(row, colsIndex[13]));
                        element.setAdditional(getCellValue(row, colsIndex[1]));

                        elements.add(element);
                    }
                }
            }
        }

        part.setElements(elements);
    }

    public static String getCellValue(Row row, int Col) {
        if (row.getCell(Col) != null) {
            if (row.getCell(Col).getCellType() == CellType.STRING) {
                return row.getCell(Col).getStringCellValue();
            } else if (row.getCell(Col).getCellType() == CellType.NUMERIC) {
                return String.valueOf(row.getCell(Col).getNumericCellValue());
            }
        }

        return null;
    }

    public static List<Message> readCSVData(String filePath) {
        try ( CSVReader reader = new CSVReader(new FileReader(filePath, StandardCharsets.UTF_8), ',')) {
            List<Message> messages = new ArrayList<>();

            // read line by line
            String[] record;
            int intStep = 0;
            while ((record = reader.readNext()) != null) {
                intStep++;
                Message msg = new Message();
//                if (record.length != 2) {
//                    System.out.println("Alert(" + intStep + "): " + Arrays.toString(record));
//                    continue;
//                }
                msg.setMessageEn(record[0]);
                msg.setMessageJp(record[1]);
                messages.add(msg);
            }

            return messages;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void getOutputPartDocs(String path, List<PacPart> partList) {
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory()) {
                    if (!isExcludeFolder(f.getName())) {
                        getOutputPartDocs(f.getAbsolutePath(), partList);
                    }
                } else {
                    // 画面組込設計書だけ対象にとなる。
                    if (f.getName().startsWith(OUTPUT_PART_FILE_PREFIX)) {
                        partList.add(getOutputPartBasicInfo(f));
                    }
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }
    }

    private static boolean isExcludeFolder(String folder) {
        for (String item : EXCLUDE_FOLDER_NAMES) {
            if (item.toUpperCase().equals(folder.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private static PacPart getOutputPartBasicInfo(File file) {
        PacPart part = new PacPart();
        String fileName = file.getName();
        String[] arrayFileName = fileName.replace(".xlsx", "").split("_");
        if (arrayFileName.length < 3) {
            System.out.println("ファイル名NG(パーツ)：" + fileName);
            return null;
        }
        part.setPartId(arrayFileName[1]);
        part.setPartName(arrayFileName[2]);
        part.setFileName(fileName);
        part.setFilePath(file.getPath());

        String prefixId = part.getPartId().substring(0, 4);
        part.setScName(ReadUtils.getSubSystemName(prefixId));
        if (part.getScName() == null) {
            System.out.println("パーツIDでNG：" + fileName);
        }

        return part;
    }

    public static List<CommonLogic> readCommonLogicListDoc() {
        List<CommonLogic> commonLogics = new ArrayList<>();
        String filePath = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\分析用テンプレート\\共通ロジック一覧.xlsx";
        try ( FileInputStream file = new FileInputStream(new File(filePath))) {
//            System.out.println("Load: " + pageInfo.getFilePath());
            try {
                //Create Workbook instance holding reference to .xlsx file
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheet("共通ロジック一覧");

                System.out.println("------------------- Common Logic Start -------------------");
                for (Row row : sheet) {
                    if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                        CommonLogic cl = new CommonLogic();
                        if (row.getCell(1).getCellType() == CellType.STRING) {
                            cl.setLogicId(row.getCell(1).getStringCellValue().trim());
                        }

                        if (row.getCell(2).getCellType() == CellType.STRING) {
                            cl.setLogicName(row.getCell(2).getStringCellValue().trim());
                        }

                        System.out.println("ID: " + cl.getLogicId() + "; Name: " + cl.getLogicName());
                        commonLogics.add(cl);
                    }
                }

                System.out.println("------------------- Common Logic End -------------------");

            } catch (IOException ex) {
                System.out.println("POI Error: " + filePath);
                System.out.println(ex.getCause());
            }
        } catch (Exception exv) {
            System.out.println("File opened: " + filePath);
        }

        return commonLogics;
    }

    public static List<CommonLogic> getCommonLogicListFileDatas() {
        if (commonLogicListFileDatas == null) {
            commonLogicListFileDatas = readCommonLogicListDoc();
        }
        return commonLogicListFileDatas;
    }

    public static void fetchCommonLogicId(CommonLogic commonLogic) {
        try {
            Optional<CommonLogic> match = getCommonLogicListFileDatas().stream().filter((CommonLogic e) -> commonLogic.getLogicName().contains(e.getLogicName())).findFirst();

            if (match.isPresent()) {
                commonLogic.setLogicId(match.get().getLogicId());
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
