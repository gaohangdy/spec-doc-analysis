/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.uxhub.analysis.design.doc;

import com.uxhub.analysis.design.doc.models.Message;
import com.uxhub.analysis.design.doc.models.Page;
import com.uxhub.analysis.design.doc.models.Part;
import com.uxhub.analysis.design.doc.utils.ReadAllParts;
import com.uxhub.analysis.design.doc.utils.ReadPacDoc;
import com.uxhub.analysis.design.doc.utils.ReadPageInfo;
import com.uxhub.analysis.design.doc.utils.ReadUtils;
import com.uxhub.analysis.design.doc.utils.RelateIODoc;
import com.uxhub.analysis.design.doc.utils.WritePageInfo;
import static com.uxhub.analysis.design.doc.utils.WritePageInfo.OUTPUT_NAME;
import static com.uxhub.analysis.design.doc.utils.WritePageInfo.TEMPLATE_NAME;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author gaohang
 */
public class AnalysisDesignDoc {

    private static final String SC_CUSTOMER = "010.会員";
    private static final String SC_PRODUCT = "020.商品";
    private static final String SC_ORDER = "030.注文・販促";
    public static final String FOLDER_NAME_PAGE = "020.画面設計書(フロント）";
    public static final String FOLDER_NAME_PART = "030.画面パーツ設計書";
    private static final String ROOT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\";
//    private static final String ROOT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\追いつき１_0111\\";
    public static final String OUTPUT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\";

    public static final String TEMPLATE_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\分析用テンプレート\\";
    public static final String i18nCsvName = "ja_jp.csv";

    public static void main(String[] args) throws FileNotFoundException, IOException {

//        analysisForEffect();

        // 外部設計書から、パーツの情報を取得する。
//        ReadAllParts.execute(ROOT_PATH);

        // 内部設計の基本データ取得
        ReadPacDoc pacDoc = new ReadPacDoc();
        pacDoc.execute();

//        List<Message> messages = ReadUtils.readCSVData(TEMPLATE_PATH + i18nCsvName);
//        writeI18nData(messages, OUTPUT_PATH, "M2_i18n_template.xlsx");
        
        // 内外部設計の関連性エクスポート
//        RelateIODoc relateIODoc = new RelateIODoc();
//        relateIODoc.execute();
    }

    private static void analysisForEffect() {
        List<Page> pageList = new ArrayList<>();
        getPageDocs(ROOT_PATH + FOLDER_NAME_PAGE, pageList, "");
        try {
            for (Page page : pageList) {
                ReadPageInfo.readFile(page);
            }

            ReadPageInfo.getCommonParts();

            System.out.println("Final");
            WritePageInfo.writeAnalysisData(pageList, ReadPageInfo.partList, OUTPUT_PATH);
//
            WritePageInfo.writePagePartElement(ReadPageInfo.partDetailList, ReadPageInfo.elementDetailList, OUTPUT_PATH);

            WritePageInfo.writePageElementsList(pageList);

        } catch (IOException ex) {
            Logger.getLogger(AnalysisDesignDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getPageDocs(String path, List<Page> pageList, String scName) {
//        String path = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\020.画面設計書(フロント）";
        File file = new File(path);
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            for (File f : fileArray) {
                if (f.isDirectory() && !"010.会員".equals(f.getName())) {
//                    System.out.println("文件夹：" + f.getAbsolutePath());
                    getPageDocs(f.getAbsolutePath(), pageList, f.getName());
                } else {
//                    System.out.println("文件：" + f.getAbsolutePath());
                    // 古いバージョンの設計書を対象外
                    if (!f.isDirectory() && !f.getName().startsWith("dummy_") && !"注文画面における静的コンテンツへの導線_20221220_01.xlsx".equals(f.getName())) {
                        pageList.add(new Page(f.getName(), f.getAbsolutePath(), scName));
                    }
                }
            }
        } else {
            System.out.println("Not exist: " + path);
        }

    }

    private static void testFetchSheet() {
        String filePath = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\030.画面パーツ設計書\\010.会員\\画面パーツ設計書_MCKNP0620_注文履歴一覧.xlsx";

        String pattern = "(パーツ設計書\\()(.*?)(\\))";
        try ( FileInputStream file = new FileInputStream(new File(filePath))) {
//            System.out.println("Load: " + pageInfo.getFilePath());
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName().trim();
                boolean isMatch = Pattern.matches(pattern, sheetName);
                if (isMatch) {
                    System.out.println(sheetName);
                    String partId = sheetName.replaceAll("(^パーツ設計書\\()+|(\\))", "");
                    System.out.println("Part id: " + partId);
                }
            }

        } catch (Exception exv) {
            System.out.println("File opened: " + filePath);
        }
    }

    public static void writeI18nData(List<Message> messages, String rootPath, String templateName) throws FileNotFoundException {
//        rootPath = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\";

        String outputPrefix = templateName.substring(0, templateName.indexOf("_template.xlsx") + 1);

        FileInputStream inputStream = new FileInputStream(new File(rootPath + "\\分析用テンプレート\\" + templateName));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet("Message");

            int startRowNum = 1;
            for (Message message : messages) {
                Row row = sheet.getRow(++startRowNum);
                if (row == null) {
                    row = sheet.createRow(startRowNum);
                }

                if (row.getCell(1) == null) {
                    row.createCell(1);
                }
                row.getCell(1).setCellValue(startRowNum - 1);

                if (row.getCell(2) == null) {
                    row.createCell(2);
                }
                row.getCell(2).setCellValue(message.getMessageEn());

                if (row.getCell(3) == null) {
                    row.createCell(3);
                }
                row.getCell(3).setCellValue(message.getMessageJp());

            }

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(rootPath + "\\export\\" + outputPrefix + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);

            outputStream.close();
        } catch (IOException | EncryptedDocumentException ex) {
            Logger.getLogger(AnalysisDesignDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
