/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.OUTPUT_PATH;
import com.uxhub.analysis.design.doc.models.PacElement;
import com.uxhub.analysis.design.doc.models.PacPage;
import com.uxhub.analysis.design.doc.models.PacParameter;
import com.uxhub.analysis.design.doc.models.PacPart;
import static com.uxhub.analysis.design.doc.utils.ReadAllParts.TEMPATE_PATH;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class ReadPacDoc {

//    private final static String DOC_ROOT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\02_Design\\02_詳設\\";
    private final static String DOC_ROOT_PATH = "C:\\Users\\gaohang\\Downloads\\Frontend_design\\";
    private final static String[] FOLDERS = {"02_画面組込む", "01_バーツ"};
    private final static String PAGE_FILE_PREFIX = "【MKC】画面組込設計書_";
    private final static String PART_FILE_PREFIX = "【MKC】パーツ詳細設計書_";
    private final static String CHECKLIST_FILE_PREFIX = "【MKC】品質チェックリスト_";
    private final static String[] EXCLUDE_FOLDER_NAMES = {"廃止", "old"};

    private final static String[] PAGE_SHEET_NAMES = {"初期処理", "画面組込", "パーツ一覧", "ページ要素一覧"};
    private final static String[] PART_SHEET_NAMES = {"パーツ概要", "ブロック詳細", "入力チェック", "イベント処理", "利用API"};

    private final static String TEMPLATE_NAME = "パラメータ一覧_template.xlsx";
    private final static String OUTPUT_FILE_PREFIX = "パラメータ一覧";

    private final static String[] TEMPLATE_SHEET_NAMES = {"画面別", "パーツ別", "画面別_API", "パーツ別_API"};

    private final List<PacPart> partFiles = new ArrayList<>();

    public void execute() {
        boolean isExport = true;
        execute(isExport);
    }

    public void execute(boolean isExport) {

        List<PacPage> pageList = getPacPages();

        for (PacPart part : partFiles) {
            getPartInfo(part);
        }

        if (isExport) {
            try {
                writeToDocument(pageList, partFiles);
            } catch (IOException ex) {
                System.out.println("Write export file error.");
            }
        }

    }
    
    public List<PacPage> getPacPages() {
        List<PacPage> pageList = new ArrayList<>();
        
        String path = DOC_ROOT_PATH + FOLDERS[0];
        getPageDocs(path, pageList);
        System.out.println("取得した画面組込設計書：" + pageList.size());
        
        getPartDocs(DOC_ROOT_PATH + FOLDERS[1], partFiles);
        System.out.println("取得したパーツ詳細設計書：" + pageList.size());

        int intStep = 0;
        for (PacPage page : pageList) {
            getPageInfo(page);

//            System.out.println("画面組込設計書：ID: " + page.getPageId() + " | Name: " + page.getFileName());
            for (PacPart part : page.getParts()) {
                getPartInfo(part);
            }

//            System.out.println("画面組込読取済：" + page.getPageId() + " " + page.getPageName());
            intStep++;
        }        
        
        return pageList;
    }

    private void getPageInfo(PacPage page) {
        System.out.println("Read: " + page.getFilePath());
        XSSFWorkbook workbook = ReadUtils.readFile(page.getFilePath());
        XSSFSheet sheetInit = workbook.getSheet(PAGE_SHEET_NAMES[0]);
        XSSFSheet sheetBuildin = workbook.getSheet(PAGE_SHEET_NAMES[1]);
        XSSFSheet sheetParts = workbook.getSheet(PAGE_SHEET_NAMES[2]);
        XSSFSheet sheetElements = workbook.getSheet(PAGE_SHEET_NAMES[3]);

        if (sheetInit == null) {
            System.out.println("Cannot find sheet [" + PAGE_SHEET_NAMES[0] + "] in [" + page.getFileName() + "]");
        } else {
            fetchSheetParameters(page, sheetInit);
            fetchSheetInitAPI(page, sheetInit);
        }
        if (sheetBuildin == null) {
            System.out.println("Cannot find sheet [" + PAGE_SHEET_NAMES[1] + "] in [" + page.getFileName() + "]");
        } else {
            fetchSheetBuildin(page, sheetBuildin);
        }
        if (sheetParts == null) {
            System.out.println("Cannot find sheet [" + PAGE_SHEET_NAMES[2] + "] in [" + page.getFileName() + "]");
        } else {
            fetchSheetParts(page, sheetParts);
        }
        if (sheetElements == null) {
            System.out.println("Cannot find sheet [" + PAGE_SHEET_NAMES[3] + "] in [" + page.getFileName() + "]");
        } else {
            fetchSheetElements(page, sheetElements);
        }
    }

    private void fetchSheetParameters(PacPage page, XSSFSheet sheet) {
        boolean isMatchParameterTitle = false;
        List<PacParameter> parameters = new ArrayList<>();

        if (page.getPageId().equals("MCCMG0060")) {
            System.out.println("Stop");
        }

        for (Row row : sheet) {
            if (row.getCell(3) != null
                    && row.getCell(3).getCellType() != CellType.BLANK
                    && row.getCell(3).getCellType() == CellType.STRING
                    && "【パラメータの取得】".equals(row.getCell(3).getStringCellValue())) {
                isMatchParameterTitle = true;
            } else if (isMatchParameterTitle) {
                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                    PacParameter parameter = new PacParameter();
                    try {
                        int nameIndex = 5;
                        parameter.setParameterName(row.getCell(nameIndex).getStringCellValue());
                        int methodIndex = ReadUtils.findCellIndex(row, nameIndex + 1);
                        parameter.setMethod(methodIndex == 0 ? null : row.getCell(methodIndex).getStringCellValue());

                        int descriptionInxdx = ReadUtils.findCellIndex(row, methodIndex + 1);
                        parameter.setDescription(descriptionInxdx == 0 ? null : row.getCell(descriptionInxdx).getStringCellValue());

                        parameters.add(parameter);
                    } catch (Exception ex) {
                        System.out.println("「初期処理」パラメータ取得NG：(Line: " + row.getRowNum() + ")" + page.getFileName());
                    }
                }
            }
        }

        page.setParameters(parameters);
    }

    private void fetchSheetInitAPI(PacPage page, XSSFSheet sheet) {
        boolean isMatchTitle = false;
        if ("トップ画面".equals(page.getPageName())) {
            System.out.println("Stp");
        }
        int fetchCols = 10;
        for (Row row : sheet) {
            if (row.getCell(3) != null
                    && row.getCell(3).getCellType() != CellType.BLANK
                    && row.getCell(3).getCellType() == CellType.STRING
                    && "【処理詳細】".equals(row.getCell(3).getStringCellValue())) {
                isMatchTitle = true;
            } else if (isMatchTitle) {
                boolean isMatchAPITitle = false;
                int apiTitlePos = 0;
                for (int intStep = 0; intStep < fetchCols; intStep++) {
                    if (row.getCell(4 + intStep) != null && row.getCell(4 + intStep).getCellType() == CellType.STRING) {
                        String cellText = row.getCell(4 + intStep).getStringCellValue();
                        if (cellText.indexOf("APIID") > 0) {
                            if (cellText.indexOf("MCAPI") > 0) {
                                //System.out.println("Match API[" + page.getFileName() + "]: " + cellText.replace("APIID：", ""));
                                page.getApis().add(cellText.replace("APIID：", "").replace("・　", "").replace("・", ""));
                            } else {
                                isMatchAPITitle = true;
                                apiTitlePos = 4 + intStep;
                                break;
                            }
                        }
                    }
                }
                if (isMatchAPITitle) {
                    for (int intStep = 0; intStep < fetchCols; intStep++) {
                        if (row.getCell(apiTitlePos + intStep + 1) != null && row.getCell(apiTitlePos + intStep + 1).getCellType() == CellType.STRING) {
                            String apiText = row.getCell(apiTitlePos + intStep + 1).getStringCellValue();
                            //System.out.println("Match API[" + page.getFileName() + "]: " + apiText);
                            page.getApis().add(apiText);
                            break;
                        }
                    }
                } else {
                    for (int intStep = 0; intStep < fetchCols; intStep++) {
                        if (row.getCell(apiTitlePos + intStep + 1) != null && row.getCell(apiTitlePos + intStep + 1).getCellType() == CellType.STRING) {
                            String apiText = row.getCell(apiTitlePos + intStep + 1).getStringCellValue();
                            if (apiText.indexOf("MCAPI") > 0) {
                                String apiId = apiText.substring(apiText.indexOf("MCAPI"), apiText.indexOf("MCAPI") + 10);
                                page.getApis().add(apiId);
                            }
                            break;
                        }
                    }                    
                }

            }
        }
    }

    private void fetchSheetBuildin(PacPage page, XSSFSheet sheet) {
        boolean isMatchPageLayout = false;
        boolean isMatchCustomizeType = false;
        boolean isMatchPageName = false;

        for (Row row : sheet) {
            if (row.getCell(3) != null
                    && row.getCell(3).getCellType() != CellType.BLANK
                    && row.getCell(3).getCellType() == CellType.STRING) {
                if (null != row.getCell(3).getStringCellValue()) {
                    switch (row.getCell(3).getStringCellValue()) {
                        case "【ページレイアウト】":
                            isMatchPageLayout = true;
                            break;
                        case "【カスタマイズ方法】":
                            isMatchCustomizeType = true;
                            break;
                        case "【M2既存/新規のページパス】":
                            isMatchPageName = true;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if (isMatchPageLayout) {
                    if (row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK && row.getCell(4).getCellType() == CellType.STRING) {
                        page.setLayoutType(row.getCell(4).getStringCellValue());
                    }
                    isMatchPageLayout = false;
                }
                if (isMatchCustomizeType) {
                    if (row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK && row.getCell(4).getCellType() == CellType.STRING) {
                        page.setCustomType(row.getCell(4).getStringCellValue());
                    }
                    isMatchCustomizeType = false;
                }
                if (isMatchPageName) {
                    if (row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK && row.getCell(4).getCellType() == CellType.STRING) {
                        page.setLayoutName(row.getCell(4).getStringCellValue());
                    }
                    isMatchPageName = false;
                }
            }
        }
    }

    private int fetchNoCol(Row row) {
        for (int intStep = 0; intStep < 10; intStep++) {
            if (row.getCell(intStep) != null 
                    && row.getCell(intStep).getCellType() == CellType.STRING 
                    && ("NO".equals(row.getCell(intStep).getStringCellValue().toUpperCase()) || "#".equals(row.getCell(intStep).getStringCellValue()))) {
                return intStep;
            }
        }
        return 0;
    }
    
    private void fetchSheetParts(PacPage page, XSSFSheet sheet) {
        boolean isPartTitle = false;
        boolean isPartStart = false;
        PacPart part = null;
        int partNoIndex = 0;
        int partIdIndex = 0;
        int parameterIndex = 0;
        int displayIndex = 0;
        int descriptionIndex = 0;
        int docIndex = 0;
        if ("MCKNG0810".equals(page.getPageId())) {
            System.out.println("Stop at MCKNG0810");
        }
        for (Row row : sheet) {
            if (!isPartTitle && partNoIndex == 0) {
                partNoIndex = fetchNoCol(row);   
                if (partNoIndex > 0) {
                    isPartTitle = true;
                    partIdIndex = ReadUtils.findCellIndex(row, partNoIndex + 1);
                    parameterIndex = ReadUtils.findCellIndex(row, partIdIndex + 1);
                    displayIndex = ReadUtils.findCellIndex(row, parameterIndex + 1);
                    descriptionIndex = ReadUtils.findCellIndex(row, displayIndex + 1);
                    docIndex = ReadUtils.findCellIndex(row, descriptionIndex + 1);
                }
            } else if (isPartTitle) {
                if (row.getCell(3) != null) {
                    try {
                        if (row.getCell(partIdIndex == 0 ? null : partIdIndex) != null
                                && !"".equals(row.getCell(partIdIndex == 0 ? null : partIdIndex).getStringCellValue())) {
//                            if (part != null && isPartStart) {
//                                page.getParts().add(part);
//                            }
                            isPartStart = true;
                            part = new PacPart();
                            part.setPartNo(row.getCell(partNoIndex).getCellType() == CellType.NUMERIC ? String.valueOf(row.getCell(partNoIndex).getNumericCellValue()) : row.getCell(partNoIndex).getStringCellValue());
                            part.setPartId(row.getCell(partIdIndex == 0 ? null : partIdIndex).getStringCellValue());
                            part.setDescription(row.getCell(descriptionIndex == 0 ? null : descriptionIndex).getStringCellValue());
                            part.setFileName(docIndex == 0 ? null : row.getCell(docIndex).getStringCellValue());
                        }

                        if (part != null && row.getCell(parameterIndex == 0 ? null : parameterIndex) != null
                                && row.getCell(parameterIndex == 0 ? null : parameterIndex).getCellType() == CellType.STRING) {
                            PacParameter parameter = new PacParameter();
                            parameter.setParameterName(row.getCell(parameterIndex == 0 ? null : parameterIndex).getStringCellValue());
                            part.getParametersPage().add(parameter);
                        }
                        
                        if (part != null) {
                            page.getParts().add(part);
                            part = null;
                        }                        
                    } catch (Exception ex) {
                        System.out.println("「パーツ一覧」パーツデータ取得NG：(Line: " + row.getRowNum() + ")" + page.getFileName());
                    }
                }
            }
        }
//        if (part != null) {
//            page.getParts().add(part);
//        }
        if ("MCKNG0810".equals(page.getPageId())) {
            System.out.println("Finish at MCKNG0810");
        }

    }

    private void fetchSheetElements(PacPage page, XSSFSheet sheet) {
        boolean isElementTitle = false;
        boolean isPartStart = false;
        PacElement part = null;
        int elementNoIndex = 3;
        int elementIdIndex = 0;
        int dispConditionIndex = 0;
        int i18nFlgIndex = 0;
        int descriptionIndex = 0;
        int hiddenIndex = 0;

        for (Row row : sheet) {
            if (row.getCell(3) != null
                    && row.getCell(3).getCellType() != CellType.BLANK
                    && row.getCell(3).getCellType() == CellType.STRING
                    && ("No".equals(row.getCell(3).getStringCellValue()) || "＃".equals(row.getCell(3).getStringCellValue()))) {
                isElementTitle = true;
                elementNoIndex = 3;
                elementIdIndex = ReadUtils.findCellIndex(row, elementNoIndex + 1);
                dispConditionIndex = ReadUtils.findCellIndex(row, elementIdIndex + 1);
                i18nFlgIndex = ReadUtils.findCellIndex(row, dispConditionIndex + 1);
                descriptionIndex = ReadUtils.findCellIndex(row, i18nFlgIndex + 1);
                hiddenIndex = ReadUtils.findCellIndex(row, descriptionIndex + 1);
            } else if (isElementTitle) {
                if (row.getCell(3) != null) {
                    try {
                        if (row.getCell(3).getCellType() == CellType.BLANK) {
//                            System.out.println("ページ要素完了の行：" + row.getRowNum());
                            break;
                        }
                        if (row.getCell(elementIdIndex == 0 ? null : elementIdIndex) != null
                                && !"".equals(row.getCell(elementIdIndex == 0 ? null : elementIdIndex).getStringCellValue())) {
                            if (part != null && isPartStart) {
                                page.getElements().add(part);
                            }
                            isPartStart = true;
                            part = new PacElement();

                            part.setElementNo(row.getCell(elementNoIndex).getCellType() == CellType.NUMERIC ? String.valueOf(row.getCell(elementNoIndex).getNumericCellValue()) : row.getCell(elementNoIndex).getStringCellValue());
                            part.setElementId(elementIdIndex == 0 ? null : row.getCell(elementIdIndex).getStringCellValue());

                            part.setDispCondition(dispConditionIndex == 0 ? null : row.getCell(dispConditionIndex).getStringCellValue());

                            String i18nFlg = dispConditionIndex == 0 ? null : row.getCell(i18nFlgIndex).getStringCellValue();
                            part.setI18nFlg("〇".equals(i18nFlg));

                            part.setDescription(descriptionIndex == 0 ? null : row.getCell(descriptionIndex).getStringCellValue());

                            String hiddenFlg = hiddenIndex == 0 ? null : row.getCell(hiddenIndex).getStringCellValue();
                            part.setHidden("〇".equals(hiddenFlg));
                        }
                    } catch (Exception ex) {
                        System.out.println("「ページ要素一覧」ページ要素データ取得NG：(Line: " + row.getRowNum() + ")" + page.getFileName());
                    }
                } else {
                    break;
                }
            }
        }
        if (part != null) {
            page.getElements().add(part);
        }
    }

    public void getPartInfo(PacPart part) {
        Optional<PacPart> partMatch = partFiles.stream().filter(e -> e.getPartId().equals(part.getPartId())).findFirst();

        if (partMatch.isPresent()) {
            part.setFilePath(partMatch.get().getFilePath());
        } else {
            System.out.println("    パーツ詳細設計書(Not found)：ID: " + part.getPartId() + " Name: " + part.getFileName());
            return;
        }

        XSSFWorkbook workbook = ReadUtils.readFile(part.getFilePath());
        XSSFSheet sheetBasic = workbook.getSheet(PART_SHEET_NAMES[0]);
        XSSFSheet sheetBlockes = workbook.getSheet(PART_SHEET_NAMES[1]);
        XSSFSheet sheetValidation = workbook.getSheet(PART_SHEET_NAMES[2]);
        XSSFSheet sheetEvents = workbook.getSheet(PART_SHEET_NAMES[3]);
        XSSFSheet sheetCallbacks = workbook.getSheet(PART_SHEET_NAMES[4]);

        if (sheetBasic == null) {
            System.out.println("Cannot find sheet [" + PART_SHEET_NAMES[0] + "] in [" + part.getFileName() + "]");
        } else {
            fetchPartSheetParameters(part, sheetBasic);
        }
        if (sheetBlockes == null) {
            System.out.println("Cannot find sheet [" + PART_SHEET_NAMES[1] + "] in [" + part.getFileName() + "]");
        } else {

        }
        if (sheetValidation == null) {
            System.out.println("Cannot find sheet [" + PART_SHEET_NAMES[2] + "] in [" + part.getFileName() + "]");
        } else {

        }
        if (sheetEvents == null) {
            System.out.println("Cannot find sheet [" + PART_SHEET_NAMES[3] + "] in [" + part.getFileName() + "]");
        } else {

        }
        if (sheetCallbacks == null) {
            System.out.println("Cannot find sheet [" + PART_SHEET_NAMES[4] + "] in [" + part.getFileName() + "]");
        } else {
            fetchPartSheetApis(part, sheetCallbacks);
        }

    }

    private void fetchPartSheetParameters(PacPart part, XSSFSheet sheet) {
        boolean isMatchParameterTitle = false;
        List<PacParameter> parameters = new ArrayList<>();
        int noIndex = 4;
        int nameIndex = 0;
        int defaultValueIndex = 0;
        int requiredIndex = 0;
        int descriptionIndex = 0;

        for (Row row : sheet) {
            if (row.getCell(3) != null
                    && row.getCell(3).getCellType() != CellType.BLANK
                    && row.getCell(3).getCellType() == CellType.STRING
                    && "【引数】".equals(row.getCell(3).getStringCellValue())) {
                isMatchParameterTitle = true;
            } else if (isMatchParameterTitle) {
                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                    nameIndex = ReadUtils.findCellIndex(row, noIndex + 1);
                    defaultValueIndex = ReadUtils.findCellIndex(row, nameIndex + 1);
                    requiredIndex = ReadUtils.findCellIndex(row, defaultValueIndex + 1);
                    descriptionIndex = ReadUtils.findCellIndex(row, requiredIndex + 1);
                }

                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.NUMERIC) {
                    PacParameter parameter = new PacParameter();
                    try {

                        parameter.setParameterName(row.getCell(nameIndex).getCellType() == CellType.BLANK ? null : row.getCell(nameIndex).getStringCellValue());
                        parameter.setDefaultValue(defaultValueIndex == 0 || row.getCell(defaultValueIndex).getCellType() == CellType.BLANK ? null
                                : row.getCell(defaultValueIndex).getCellType() == CellType.NUMERIC ? String.valueOf(row.getCell(defaultValueIndex).getNumericCellValue())
                                : row.getCell(defaultValueIndex).getStringCellValue());
                        parameter.setRequired(requiredIndex == 0 || row.getCell(requiredIndex).getCellType() == CellType.BLANK ? false : "必須".equals(row.getCell(requiredIndex).getStringCellValue()));
                        parameter.setDescription(descriptionIndex == 0 || row.getCell(descriptionIndex).getCellType() == CellType.BLANK ? null : row.getCell(descriptionIndex).getStringCellValue());

                        parameters.add(parameter);
                    } catch (Exception ex) {
                        System.out.println("「パーツ概要」引数取得NG：(Line: " + row.getRowNum() + ")" + part.getFileName());
                    }
                }
            }
        }

        part.setParametersPart(parameters);
    }

    private void fetchPartSheetApis(PacPart part, XSSFSheet sheet) {
        boolean isMatchTitle = false;
        int methodIndex = 0;
        
        for (Row row : sheet) {
            if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.STRING && "NO".equals(row.getCell(3).getStringCellValue().toUpperCase())) {
                isMatchTitle = true;
                methodIndex = ReadUtils.findCellIndex(row, 3 + 1);
            } else if (isMatchTitle) {
                if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.NUMERIC) {
                    String apiId = row.getCell(methodIndex) != null && row.getCell(methodIndex).getCellType() != CellType.BLANK ? row.getCell(methodIndex).getStringCellValue() : "NO SELECT";
                    if (!"NO SELECT".equals(apiId)) {
                        part.getApis().add(apiId);
                    }
//                    System.out.println("Parts API: " + apiId + "[" + part.getPartId() + "]");
                }
            }
        }
    }

    private void getPageDocs(String path, List<PacPage> pageList) {
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory()) {
                    if (!isExcludeFolder(f.getName())) {
                        getPageDocs(f.getAbsolutePath(), pageList);
                    }
                } else {
                    // 画面組込設計書だけ対象にとなる。
                    if (f.getName().startsWith(PAGE_FILE_PREFIX)) {
                        pageList.add(getPageBasicInfo(f));
                    }
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }
    }

    private void getPartDocs(String path, List<PacPart> partList) {
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory()) {
                    if (!isExcludeFolder(f.getName())) {
                        getPartDocs(f.getAbsolutePath(), partList);
                    }
                } else {
                    // 画面組込設計書だけ対象にとなる。
                    if (f.getName().startsWith(PART_FILE_PREFIX)) {
                        partList.add(getPartBasicInfo(f));
                    }
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }
    }

    private boolean isExcludeFolder(String folder) {
        for (String item : EXCLUDE_FOLDER_NAMES) {
            if (item.toUpperCase().equals(folder.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private PacPage getPageBasicInfo(File file) {
        PacPage page = new PacPage();
        String fileName = file.getName();
        String[] arrayFileName = fileName.replace(".xlsx", "").split("_");
        if (arrayFileName.length != 3) {
            System.out.println("ファイル名NG：" + fileName);
            return null;
        }
        page.setPageId(arrayFileName[1]);
        page.setPageName(arrayFileName[2]);
        page.setFileName(fileName);
        page.setFilePath(file.getPath());

        String prefixId = page.getPageId().substring(0, 4);
        page.setScName(ReadUtils.getSubSystemName(prefixId));
        if (page.getScName() == null) {
            System.out.println("画面IDでNG：" + fileName);
        }
        return page;
    }

    private PacPart getPartBasicInfo(File file) {
        PacPart part = new PacPart();
        String fileName = file.getName();
        String[] arrayFileName = fileName.replace(".xlsx", "").split("_");
        if (arrayFileName.length < 3) {
            System.out.println("ファイル名NG(パーツ)：" + fileName);
            return null;
        }
        part.setPartId(arrayFileName[1]);
        if (arrayFileName[2] == null) {
            System.out.println("Stop");
        }
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

    private void writeToDocument(List<PacPage> pageList, List<PacPart> partList) throws IOException {

        pageList.sort(Comparator.comparing(PacPage::getPageId));

        FileInputStream inputStream = new FileInputStream(new File(TEMPATE_PATH + TEMPLATE_NAME));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheetPage = workbook.getSheet(TEMPLATE_SHEET_NAMES[0]);
            writePageDatas(sheetPage, pageList);

            Sheet sheetPart = workbook.getSheet(TEMPLATE_SHEET_NAMES[1]);
            writePartDatas(sheetPart, partList);

            Sheet sheetPageApi = workbook.getSheet(TEMPLATE_SHEET_NAMES[2]);
            writePageApis(sheetPageApi, pageList);

            Sheet sheetPartApi = workbook.getSheet(TEMPLATE_SHEET_NAMES[3]);
            writePartApis(sheetPartApi, partList);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(OUTPUT_PATH + "\\export\\" + OUTPUT_FILE_PREFIX + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writePageDatas(Sheet sheet, List<PacPage> partList) {
        int startRowNum = 1;
        for (PacPage page : partList) {
            for (PacParameter parameter : page.getParameters()) {
                int intStartCol = 1;

                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

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

                intStartCol++;
                // -- パラメータID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue("-");

                intStartCol++;
                // -- パラメータ名
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                if ("".equals(parameter.getParameterName()) || parameter.getParameterName() == null) {
//                    row.getCell(intStartCol)
                }
                row.getCell(intStartCol).setCellValue(parameter.getParameterName());

                intStartCol++;
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(parameter.getMethod());
            }
        }
    }

    private static void writePartDatas(Sheet sheet, List<PacPart> partList) {
        int startRowNum = 1;

        for (PacPart part : partList) {
            for (PacParameter parameter : part.getParametersPart()) {
                int intStartCol = 1;

                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }
                // No.
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(startRowNum - 1);

                intStartCol++;
                // Sub system
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(ReadUtils.getSubSystemName(part.getPartId()));

                intStartCol++;
                // Part ID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(part.getPartId());

                intStartCol++;
                // Part 名
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(part.getPartName());

                intStartCol++;
                // -- パラメータID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue("-");

                intStartCol++;
                // -- パラメータ名
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(parameter.getParameterName());

                intStartCol++;
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(parameter.getDefaultValue());

                intStartCol++;
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(parameter.isRequired() ? "〇" : "-");

                intStartCol++;
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(parameter.getDescription());

            }
        }
    }

    private static void writePageApis(Sheet sheet, List<PacPage> partList) {
        int startRowNum = 1;
        for (PacPage page : partList) {
            for (String apiId : page.getApis()) {
                int intStartCol = 1;

                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

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

                intStartCol++;
                // -- API ID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(apiId);
            }
        }
    }

    private static void writePartApis(Sheet sheet, List<PacPart> partList) {
        int startRowNum = 1;

        for (PacPart part : partList) {
            for (String apiId : part.getApis()) {
                int intStartCol = 1;

                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }
                // No.
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(startRowNum - 1);

                intStartCol++;
                // Sub system
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(ReadUtils.getSubSystemName(part.getPartId()));

                intStartCol++;
                // Part ID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(part.getPartId());

                intStartCol++;
                // Part 名
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(part.getPartName());

                intStartCol++;
                // -- API ID
                if (row.getCell(intStartCol) == null) {
                    row.createCell(intStartCol);
                }
                row.getCell(intStartCol).setCellValue(apiId);
            }
        }
    }
}
