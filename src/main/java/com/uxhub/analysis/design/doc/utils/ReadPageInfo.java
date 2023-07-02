/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import com.uxhub.analysis.design.doc.models.CommonLogic;
import com.uxhub.analysis.design.doc.models.Page;
import com.uxhub.analysis.design.doc.models.Part;
import com.uxhub.analysis.design.doc.models.PartDetail;
import com.uxhub.analysis.design.doc.models.PartInPage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author gaohang
 */
public class ReadPageInfo {

    public static final String FOLDER = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\030.画面パーツ設計書\\";
//    public static final String FOLDER = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\追いつき１_0111\\030.画面パーツ設計書\\"; //Test for cat3
    public static final String FILE_NAME = "画面設計書（フロント）_MCSHG0030_商品詳細画面.xlsx";
    public static final String INITIAL_PROCESS = "初期処理";
    public static final String FINAL_SECTION = "画面パラメータ一覧";

    public static List<Part> partList = new ArrayList<>();

    private static final List<Part> partFileList = new ArrayList<>();

    public static final List<PartDetail> partDetailList = new ArrayList<>();
    public static final List<PartDetail> elementDetailList = new ArrayList<>();

    private static Page subPartsPage = new Page();

    public static void readFile(Page pageInfo) throws FileNotFoundException, IOException {
//        Page pageInfo = new Page();
        //Create Workbook instance holding reference to .xlsx file
        try ( FileInputStream file = new FileInputStream(new File(pageInfo.getFilePath()))) {
//            System.out.println("Load: " + pageInfo.getFilePath());
            try {
                //Create Workbook instance holding reference to .xlsx file
                XSSFWorkbook workbook = new XSSFWorkbook(file);

                if ("画面設計書（フロント）_MCSHG0030_商品詳細画面.xlsx".equals(pageInfo.getFileName())) {
                    System.out.println("Debug stop!!!");
                }

                // 画面のファイル名からIDと名称を取得する。
                String[] arrayFileName = pageInfo.getFileName().replace(".xlsx", "").split("_");
                if (arrayFileName.length > 2) {
                    pageInfo.setPageId(arrayFileName[1].trim());
                    pageInfo.setPageName(arrayFileName[2].trim());
                }

                // 画面設計書の「共通ロジック定義書」シートに記載した共通ロジック情報を取得する。
                getCommonLogicInPage(workbook, pageInfo);

                if ("MCHSG0020".equals(pageInfo.getPageId())) {
                    System.out.println("Stop at MCHSG0020");
                }
                
                // 外部設計：画面概要シートのデータを取得
                getOverview(workbook, pageInfo);
                // 外部設計：画面要素一覧シートのデータを取得
                getParts(workbook, pageInfo);

                // 画面パーツ設計書にサブパーツのシートを取得
                getPartsNotInList(workbook, pageInfo, null);
                for (Part part : pageInfo.getPartList()) {
                    readPart(part, pageInfo, workbook);
                }
                //Get first/desired sheet from the workbook
//            XSSFSheet sheet = workbook.getSheetAt(0);
            } catch (IOException ex) {
                System.out.println("POI Error: " + pageInfo.getFilePath());
                System.out.println(ex.getCause());
            }
        } catch (Exception exv) {
            System.out.println("File opened: " + pageInfo.getFilePath());
        }
    }

    private static void readPart(Part part, Page pageInfo, XSSFWorkbook workbook) throws IOException {
        if (!partList.stream().noneMatch(item -> item.getPartId().equals(part.getPartId()))) {
            return;
        }

        XSSFWorkbook workbookUsed = workbook;

        String partElementName = "パーツ設計書(" + part.getPartId() + ")";
        String partEventName = "イベント定義書(" + part.getPartId() + ")";
        XSSFSheet sheetPart = workbook.getSheet(partElementName);
        XSSFSheet sheetPartEvent = workbook.getSheet(partEventName);
        // 共通パーツ（画面設計書に「パーツ設計書(XXXXXX)」を見つかれない）の場合、パーツ設計書からシートデータを取得する。
        if (sheetPart == null) {
            sheetPart = workbook.getSheet(partElementName + " ");
            if (sheetPartEvent == null) {
                sheetPartEvent = workbook.getSheet(partEventName + " ");
            }
            if (sheetPart == null) {
                if (partFileList.isEmpty()) {
                    getPartDocs(FOLDER, partFileList, "");
                }
                String partFileName = "画面パーツ設計書_" + part.getPartId() + "_" + part.getPartName() + ".xlsx";
                //Optional<Part> partMatch = partFileList.stream().filter(e -> e.getFileName().equals(partFileName)).findFirst();
                Optional<Part> partMatch = partFileList.stream().filter(e -> e.getFileName().indexOf(part.getPartId()) > 0).findFirst();

                if (!partMatch.isPresent()) {
                    // 「注文・販促」のファイル名が違いですので
                    String partFileNameOrder = "画面パーツ設計書（フロント）_" + part.getPartId() + "_" + part.getPartName() + ".xlsx";
                    Optional<Part> orderPartMatch = partFileList.stream().filter(e -> e.getFileName().equals(partFileNameOrder)).findFirst();
                    if (!orderPartMatch.isPresent()) {
                        System.out.println("Parts File not found: " + partFileName + ", Use Page: " + pageInfo.getFileName());
                        return;
                    }
                }

                //　パーツの設計書ファイルがあるの場合、共通パーツになる。
                part.setCommonPart(true);

                try ( FileInputStream filePart = new FileInputStream(new File(partMatch.get().getFilePath()))) {
                    XSSFWorkbook workbookPart = new XSSFWorkbook(filePart);
                    workbookUsed = workbookPart;
                    sheetPart = workbookPart.getSheet("パーツ設計書") == null ? workbookPart.getSheet(partElementName) : workbookPart.getSheet("パーツ設計書");
                    sheetPartEvent = workbookPart.getSheet("イベント定義書") == null ? workbookPart.getSheet(partEventName) : workbookPart.getSheet("イベント定義書");
                    getPartsNotInList(workbookUsed, subPartsPage, part.getPartId());
                    if (!subPartsPage.getPartList().isEmpty()) {
                        subPartsPage.setPageId(pageInfo.getPageId());
                        subPartsPage.setPageName(pageInfo.getPageName());
                        subPartsPage.setFileName(pageInfo.getFileName());
                        for (Part subPart : subPartsPage.getPartList()) {
                            readPart(subPart, subPartsPage, workbookUsed);
                        }
                        subPartsPage = new Page();
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException: " + partMatch.get().getFilePath());
                }
            }
        }
        if (sheetPart == null) {
            System.out.println("Sheet not found: " + partElementName + ", Use Page: " + pageInfo.getFileName());
        } else {
            getPartDetail(sheetPart, part, workbookUsed);
        }
        if (sheetPartEvent == null) {
            System.out.println("Sheet not found: " + partEventName + ", Use Page: " + pageInfo.getFileName());
        } else {
            getPartEvent(sheetPartEvent, part);
        }

        part.getParentPageList().add(new PartInPage(pageInfo.getPageId(), pageInfo.getPageName(), part.getUpdateType()));
        partList.add(part);
    }

    public static void getCommonParts() throws IOException {
        List<Part> commonPartList = partFileList.stream().filter(item -> !partList.stream()
                .map(e -> e.getPartId())
                .collect(Collectors.toList())
                .contains(item.getPartId()))
                .collect(Collectors.toList());

        for (Part part : commonPartList) {
            try ( FileInputStream filePart = new FileInputStream(new File(part.getFilePath()))) {
                System.out.println("Cat3: " + part.getFilePath());
                XSSFWorkbook workbookPart = new XSSFWorkbook(filePart);

                String partElementName = "パーツ設計書(" + part.getPartId() + ")";
                String partEventName = "イベント定義書(" + part.getPartId() + ")";

                XSSFSheet sheetPart = workbookPart.getSheet("パーツ設計書") == null ? workbookPart.getSheet(partElementName) : workbookPart.getSheet("パーツ設計書");
                XSSFSheet sheetPartEvent = workbookPart.getSheet("イベント定義書") == null ? workbookPart.getSheet(partEventName) : workbookPart.getSheet("イベント定義書");

                getPartsNotInList(workbookPart, subPartsPage, part.getPartId());
                if (!subPartsPage.getPartList().isEmpty()) {
                    subPartsPage.setPageId("");
                    subPartsPage.setPageName("");
                    subPartsPage.setFileName(part.getFilePath());
                    for (Part subPart : subPartsPage.getPartList()) {
//                            readPart(subPart, subPartsPage, workbookUsed);
                        System.out.println("*******" + subPart.getPartId());
                    }
                    subPartsPage = new Page();
                }

                if (sheetPart == null) {
                    System.out.println("Sheet not found: " + partElementName + ", Use Page: " + part.getFileName());
                } else {
                    getPartDetail(sheetPart, part, workbookPart);
                }
                if (sheetPartEvent == null) {
                    System.out.println("Sheet not found: " + partEventName + ", Use Page: " + part.getFileName());
                } else {
                    getPartEvent(sheetPartEvent, part);
                }

                partList.add(part);

            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + part.getFilePath());
            }
        }

    }

    private static void getPartDetail(XSSFSheet sheetPart, Part part, XSSFWorkbook workbook) {
        boolean elementStart = false;
        // 固定文言
        int staticCnt = 0;
        // 出力項目
        int outputCnt = 0;
        // 入力項目
        int inputCnt = 0;
        int validationCnt = 0;
        int eventCnt = 0;

        int noColIndex = 0;
        int elNmColIndex = -1;
        int elIDColIndex = -1;
        int elTypeColIndex = -1;
        int ioTypeColIndex = -1;
        int validationColIndex = -1;
        int formatColIndex = -1;
        int eventColIndex = -1;
        int descriptionColIndex = -1;
        int commentColIndex = -1;

        for (Row row : sheetPart) {
            // 「項目説明一覧」のNOを探す
            if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING && "NO".equals(row.getCell(0).getStringCellValue().toUpperCase())) {
                elementStart = true;
                noColIndex = 0;
                elNmColIndex = ReadUtils.findCellIndex(row, noColIndex + 1);
                elIDColIndex = ReadUtils.findCellIndex(row, elNmColIndex + 1);
                elTypeColIndex = ReadUtils.findCellIndex(row, elIDColIndex + 1);
                ioTypeColIndex = ReadUtils.findCellIndex(row, elTypeColIndex + 1);
                validationColIndex = ReadUtils.findCellIndex(row, ioTypeColIndex + 1);
                formatColIndex = ReadUtils.findCellIndex(row, validationColIndex + 1);
                eventColIndex = ReadUtils.findCellIndex(row, formatColIndex + 1);
                descriptionColIndex = ReadUtils.findCellIndex(row, eventColIndex + 1);
                commentColIndex = ReadUtils.findCellIndex(row, descriptionColIndex + 1);
            }
            if (elementStart) {
                if ((row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) || (row.getCell(elNmColIndex) != null && row.getCell(elNmColIndex).getCellType() != CellType.BLANK)) {
                    if (row.getCell(17) != null && "固定文言".equals(row.getCell(17).getStringCellValue())) {
                        staticCnt++;
                    }
                    if (row.getCell(20) != null && "O".equals(row.getCell(20).getStringCellValue())) {
                        outputCnt++;
                    }
                    if (row.getCell(20) != null && ("I".equals(row.getCell(20).getStringCellValue()) || "I/O".equals(row.getCell(20).getStringCellValue()))) {
                        inputCnt++;
                        if (hasValidation(row)) {
                            validationCnt++;
                        }
                    }

//                    System.out.println("Event text: " + row.getCell(eventColIndex).getStringCellValue());
                    // 「イベント」指定する。
                    if (row.getCell(formatColIndex) != null && row.getCell(formatColIndex).getCellType() != CellType.BLANK && !"-".equals(row.getCell(formatColIndex).getStringCellValue())
                            && !"".equals(row.getCell(formatColIndex).getStringCellValue().trim()) && !"イベント".equals(row.getCell(formatColIndex).getStringCellValue())) {
                        eventCnt++;
                    }
                }
            }
        }
        part.setStaticCnt(staticCnt);
        part.setOutputCnt(outputCnt);
        part.setInputCnt(inputCnt);
        part.setValidationCnt(validationCnt);
        part.setEventCnt(eventCnt);
    }

    private static void getPartsNotInList(XSSFWorkbook workbook, Page pageInfo, String excludePartId) throws IOException {
        String pattern = "(パーツ設計書\\()(.*?)(\\))";

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet currentSheet = workbook.getSheetAt(i);
            String sheetName = currentSheet.getSheetName().trim();
            boolean isMatch = Pattern.matches(pattern, sheetName);
            if (isMatch) {
                Part part = new Part();
                String partId = sheetName.replaceAll("(^パーツ設計書\\()+|(\\))", "");
                if (partId.equals(excludePartId)) {
                    continue;
                }

                // Check if part id in part list?
                Optional<Part> partMatch = pageInfo.getPartList().stream().filter(e -> e.getPartId().equals(partId)).findFirst();

                if (partMatch.isPresent()) {
                    continue;
                }

                part.setPartId(partId);

                if ("MCKNP0830".equals(partId)) {
                    System.out.println("match");
                }

                part.setPartName(fetchPartName(currentSheet.getRow(0)));

                pageInfo.getPartList().add(part);
                //System.out.println("Add Part (" + partId + ") to " + pageInfo.getFileName());
            }
        }
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

    private static void getPartEvent(XSSFSheet sheetPart, Part part) {
        boolean methodStart = false;
        int methodCnt = 0;
        for (Row row : sheetPart) {
            if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING && "1.初期表示".equals(row.getCell(0).getStringCellValue())) {
                methodStart = true;
            }
            if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING && "2.画面動作".equals(row.getCell(0).getStringCellValue())) {
                break;
            }

            if (methodStart && row.getCell(1) != null && row.getCell(1).getCellType() != CellType.BLANK) {
                methodCnt++;
            }
        }
        part.setInitMethodCnt(methodCnt);
    }

    private static boolean hasValidation(Row row) {
        int[] validationCols = {22, 24, 26, 28, 30, 32, 34, 36};
        for (int cellIndex : validationCols) {
            if (row.getCell(cellIndex).getCellType() != CellType.BLANK) {
                if (row.getCell(cellIndex).getCellType() == CellType.STRING && !"-".equals(row.getCell(cellIndex).getStringCellValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void getOverview(XSSFWorkbook workbook, Page pageInfo) {
        XSSFSheet sheet = workbook.getSheet("画面概要");

        if (pageInfo.getPageId().isEmpty()) {
            pageInfo.setPageId(sheet.getRow(0).getCell(16).getStringCellValue());
        }
        if (pageInfo.getPageName().isEmpty()) {
            pageInfo.setPageName(sheet.getRow(0).getCell(28).getStringCellValue());
        }

        //Iterate through each rows one by one
        int intStep = 0;
        boolean sectionStartInitalProcess = false;
        boolean lastSection = false;
        boolean lastSectionStart = false;

        int initalProcessCount = 0;
        int parameterCount = 0;
        for (Row row : sheet) {
            intStep++;

            if (lastSectionStart && (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK)) {
                pageInfo.setMethodCnt(initalProcessCount);
                break;
            }

            if (row.getCell(0) != null) {
//                if (row.getCell(0).getCellType() == CellType.STRING && !"".equals(row.getCell(0).getStringCellValue())) {

                if (lastSectionStart && row.getCell(0).getCellType() != CellType.BLANK) {
                    if (!"".equals(row.getCell(2).getStringCellValue()) && !"なし".equals(row.getCell(2).getStringCellValue())) {
                        parameterCount++;
                        continue;
                    }
                }

                String title = row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : null;
                if (INITIAL_PROCESS.equals(title)) {
                    sectionStartInitalProcess = true;
                    continue;
                }

                if (FINAL_SECTION.equals(title)) {
                    lastSection = true;
//                        continue;
                }

                if (lastSection && row.getCell(0).getCellType() == CellType.STRING && "NO".equals((row.getCell(0).getStringCellValue().toUpperCase()))) {
                    lastSectionStart = true;
//                        continue;
                }

//                }
            }

            if (sectionStartInitalProcess) {
                if (row.getCell(1) != null && !"".equals(row.getCell(1).getStringCellValue())) {
                    initalProcessCount++;
                }

                // 「画面概要」シートに記載する共通ロジックを抽出
                fetchCommonLogic(row, pageInfo);
            }
        }
        pageInfo.setMethodCnt(initalProcessCount);
        pageInfo.setParamterCnt(parameterCount);
    }

    private static void fetchCommonLogic(Row row, Page pageInfo) {
        int colCnt = 60;
        String pattern = ".*「(.*?)」.*";
        for (int intStep = 0; intStep < colCnt; intStep++) {
            if (row.getCell(intStep) != null && row.getCell(intStep).getCellType() == CellType.STRING) {
                String text = row.getCell(intStep).getStringCellValue();
                if (text.contains("共通ロジック：") || text.contains("共通ロジック:")) {
                    boolean isMatch = Pattern.matches(pattern, text);
                    if (isMatch) {
                        String commonLogicName = text.replaceAll(".*「+|」.*", "");
                        System.out.println("共通ロジック: " + commonLogicName + " From:" + text);
                        if (!"".equals(commonLogicName.trim())) {
                            Optional<CommonLogic> match = pageInfo.getCommonLogics().stream().filter(e -> e.getLogicName().equals(commonLogicName)).findFirst();

                            if (!match.isPresent()) {
                                CommonLogic commonLogic = new CommonLogic();
                                if ("共通ロジック".equals(commonLogicName)) {
                                    System.out.println("Stop logicName");
                                }
                                commonLogic.setLogicName(commonLogicName);
                                System.out.println("CommonLogic Matched Start：" + commonLogic.getLogicName());
                                if ("No.8 商品の販売可否".equals(commonLogic.getLogicName())) {
                                    System.out.println("Stop");
                                }
                                ReadUtils.fetchCommonLogicId(commonLogic);
                                System.out.println("共通ロジック一覧.xlsxから取得しました：" + commonLogic.getLogicName());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void getParts(XSSFWorkbook workbook, Page pageInfo) {
        XSSFSheet sheet = workbook.getSheet("画面要素一覧");
        if (sheet == null) {
            System.out.println("Not Found [画面要素一覧]: " + pageInfo.getFileName());
            return;
        }
        boolean sectionStartPartsNo = false;
        int intStep = 0;
        int partsCount = 0;
        int elementCount = 0;
        for (Row row : sheet) {
            intStep++;

            if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING && "要素番号".equals(row.getCell(0).getStringCellValue())) {
                sectionStartPartsNo = true;
                continue;
            }

            if (sectionStartPartsNo && row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) {
                if ("パーツ".equals(row.getCell(17).getStringCellValue()) && !"-".equals(row.getCell(2).getStringCellValue())) {
                    Part part = new Part();

                    String partId = row.getCell(2).getStringCellValue();
                    if ("MCXXPXX01".equals(partId)) {
                        partId = "MCACP0010";
                    }
                    if ("MCXXPXX02".equals(partId)) {
                        partId = "MCACP0030";
                    }
                    if ("MCXXPXX12".equals(partId)) {
                        partId = "MCACP0020";
                    }
                    if ("MCCMP0010".equals(partId)) {
                        System.out.println("Matched ID:" + partId + ", Page:" + pageInfo.getPageId());
                    }                    
                    
                    part.setPartId(partId);
                    part.setPartName(row.getCell(7).getStringCellValue());
                    part.setUpdateType(row.getCell(21).getStringCellValue());
                    pageInfo.getPartList().add(part);

                    partsCount++;

                    partDetailList.add(getElementDetailInfo(partId, elementCount, pageInfo, row, false));

                }
                if ("ページ要素".equals(row.getCell(17).getStringCellValue()) || "-".equals(row.getCell(17).getStringCellValue())) {
                    elementCount++;
                    PartDetail partDetail = getElementDetailInfo(null, elementCount, pageInfo, row, true);
                    pageInfo.getElementList().add(partDetail);
                    elementDetailList.add(partDetail);
                }
            }
        }
//        System.out.println("Matched last line:" + intStep);
//        System.out.println("パーツ" + ":" + partsCount);
//        System.out.println("ページ要素" + ":" + elementCount);
        pageInfo.setPartsCnt(partsCount);
        pageInfo.setElementCnt(elementCount);
    }

    private static void getPartDocs(String path, List<Part> pageList, String scName) {
//        String path = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\020.画面設計書(フロント）";
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory() && !"010.会員".equals(f.getName())) {
//                    System.out.println("文件夹：" + f.getAbsolutePath());
                    getPartDocs(f.getAbsolutePath(), pageList, f.getName());
                } else {
//                    System.out.println("文件：" + f.getAbsolutePath());
                    String[] names = f.getName().replace(".xlsx", "").split("_");
                    String partId = "";
                    String partName = "";
                    if (names.length == 3) {
                        partId = names[1].trim();
                        partName = names[2].trim();
                    }
                    pageList.add(new Part(partId, partName, f.getName(), f.getAbsolutePath(), scName));
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }
    }

    private static PartDetail getElementDetailInfo(String partId, int elementCount, Page pageInfo, Row row, boolean isElement) {
        PartDetail elementDetail = new PartDetail();
        elementDetail.setPartId(isElement ? pageInfo.getPageId() + "E" + String.format("%04d", elementCount) : partId);
        elementDetail.setPartName(row.getCell(7).getStringCellValue());
        elementDetail.setUpdateType(row.getCell(21).getStringCellValue());
        elementDetail.setEventDescription(row.getCell(43).getStringCellValue());
        elementDetail.setParentPage(pageInfo);

        return elementDetail;
    }

    // 画面設計書に「共通ロジック定義書」シートから画面に使う共通ロジックの情報を取得する。
    private static void getCommonLogicInPage(XSSFWorkbook workbook, Page pageInfo) {
        XSSFSheet sheet = workbook.getSheet("共通ロジック定義書");

        if (sheet != null) {
            System.out.println("Stop at 共通ロジック定義書");
        } else {
            return;
        }

        int intStartRow = 3;
        for (Row row : sheet) {
            if (row.getRowNum() < intStartRow) {
                continue;
            }

            if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
                CommonLogic commonLogic = new CommonLogic();
                commonLogic.setLogicName(row.getCell(0).getStringCellValue().trim());
                commonLogic.setPageScope(true);
                pageInfo.getCommonLogics().add(commonLogic);
            }
        }
    }
}
