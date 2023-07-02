/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc;

import com.uxhub.analysis.design.doc.models.LogFile;
import com.uxhub.analysis.design.doc.models.SecurityLog;
import com.uxhub.analysis.design.doc.models.SecurityLogItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gaohang
 */
public class FormatM2Log {

    private final static String FILE_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\03_開発・単体テスト\\temp\\サーバサイドの入力項目チェック\\ログファイルExcel化";
    private final static String OUTPUT_FILE_TEMPLATE = FILE_PATH + "\\" + "scan_report_template.xlsx";
    private final static String OUTPUT_FILE_PREFIX = "scan_report";
    private final static int START_ROW_NUM = 2;

    private final static String SOURCE_PATH = "C:\\work\\source\\mkc\\dev-repository\\mccm-ec-app\\app";

    public static void main(String[] args) throws FileNotFoundException, IOException {
//        String test = "                            <big><?= $mailOrderPriceIncludeTax ?>円</big>";
//        if (isEscapeHtmlSource(test)) {
//            System.out.println("Matched!");
//            return;
//        }
        
        
        boolean isXSS = false;
        String[] fileNames = {"scan_module_9.log", "scan_theme_9.log"};

        List<LogFile> logFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            boolean isMatchFile = false;
            List<SecurityLog> logs = new ArrayList<>();
            String filePath = FILE_PATH + "\\" + fileName;
            File file = new File(filePath);

            BufferedReader br
                    = new BufferedReader(new FileReader(file));

            SecurityLog securityLog = null;

            String st;
            while ((st = br.readLine()) != null) {
                if (!isMatchFile) {
                    isMatchFile = checkSourceFileLine(st);
                    if (isMatchFile) {
                        if (securityLog != null) {
                            logs.add(securityLog);
                        }
                        securityLog = new SecurityLog();
                        sourceFileLine(securityLog, st);
                    }
                } else {
                    if (checkSourceFileLine(st)) {
                        logs.add(securityLog);
                        securityLog = new SecurityLog();
                        sourceFileLine(securityLog, st);
                    }
                    sourceMessageLine(securityLog, st);
                }
            }

            if (securityLog != null) {
                logs.add(securityLog);
            }

            if (!logs.isEmpty()) {
                LogFile logFile = new LogFile();
                logFile.setFileName(fileName);
                logFile.setSheetName(fileName.split("_")[1]);
                logFile.setLogs(logs);
                logFiles.add(logFile);
            }
//            System.out.println("Read information from file:" + filePath);

            System.out.println("End execute log anylisis");
        }

        if (!logFiles.isEmpty()) {
            writeExcel(logFiles, isXSS);
        }
    }

    private static boolean checkSourceFileLine(String text) {
        String pattern = "^FILE: (.*?)"; //"(パーツ設計書\\()(.*?)(\\))";
        return Pattern.matches(pattern, text);
    }

    private static boolean sourceFileLine(SecurityLog securityLog, String text) {
        String pattern = "^FILE: (.*?)"; //"(パーツ設計書\\()(.*?)(\\))";

        boolean isMatch = Pattern.matches(pattern, text);
        if (isMatch) {
            String fileInfo = text.replaceAll("(^^FILE: )", "");
//            System.out.println(text);
            securityLog.setFile(fileInfo);
//            String partId = sheetName.replaceAll("(^パーツ設計書\\()+|(\\))", "");
        }

        return isMatch;
    }

    private static boolean sourceMessageLine(SecurityLog securityLog, String text) {
        String pattern = ".* \\| WARNING \\| .*|.* \\| ERROR \\| .*";

        boolean isMatch = Pattern.matches(pattern, text);
        if (isMatch) {
            String[] arryMessage = text.split("\\|");
            if (arryMessage.length == 3) {
                if (securityLog.getItems() == null) {
                    securityLog.setItems(new ArrayList<>());
                }
                SecurityLogItem item = new SecurityLogItem();
                item.setPos(arryMessage[0].trim());
                item.setType(arryMessage[1].trim());
                item.setMessage(arryMessage[2].trim());
                securityLog.getItems().add(item);
            }
//            System.out.println(text);
//            String partId = sheetName.replaceAll("(^パーツ設計書\\()+|(\\))", "");
        } else if (text.startsWith("    |       | ")) {
            String[] arryMessage = text.split("\\|");
            String message = securityLog.getItems().get(securityLog.getItems().size() - 1).getMessage() + "\n" + arryMessage[2].trim();
            securityLog.getItems().get(securityLog.getItems().size() - 1).setMessage(message);
        }

        return isMatch;
    }

    private static void writeExcel(List<LogFile> logFiles, boolean isXSS) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(new File(OUTPUT_FILE_TEMPLATE));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            for (LogFile logFile : logFiles) {
                Sheet sheet = workbook.getSheet(logFile.getSheetName());
                writeDatas(sheet, logFile.getLogs(), isXSS);
            }

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(FILE_PATH + "\\" + OUTPUT_FILE_PREFIX + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writeDatas(Sheet sheet, List<SecurityLog> logs, boolean isXSS) throws FileNotFoundException, IOException {
        int intStep = 0;
        System.out.println("Sheet: " + sheet.getSheetName());
        
        for (SecurityLog log : logs) {
            String scope = log.getFile().contains("/view/adminhtml") ? "Backend" : "Frontend";
            File file = new File(SOURCE_PATH + getSourceCodeFilePath(log.getFile()));
            if (!file.exists()) {
                continue;
            }
            for (SecurityLogItem item : log.getItems()) {
                if (isXSS && !item.getMessage().contains("Unescaped output detected.")) {
                    continue;
                }
                
                String[] arryPath = log.getFile().split("/");
                String module = log.getFile().indexOf("/app/design/frontend") > 0 ? arryPath[9] : arryPath[6] + "_" + arryPath[7];                
                if ("Mccm_Test".equals(module)) {
                    continue;
                }

                Row row = sheet.getRow(START_ROW_NUM + intStep);
//                System.out.println("Line:" + intStep);
                row.getCell(1).setCellValue(intStep + 1);
                row.getCell(2).setCellValue(module);
                row.getCell(3).setCellValue(log.getFile());
                row.getCell(4).setCellValue(arryPath[arryPath.length - 1].split("\\.")[1]);
                row.getCell(5).setCellValue(item.getType());
                row.getCell(6).setCellValue(item.getPos());
                row.getCell(7).setCellValue(item.getMessage());
                String sourceCode = readSourceCodeLine(file, Integer.parseInt(item.getPos()));
                if (sourceCode != null) {
                    row.getCell(8).setCellValue(sourceCode);
                }
                
                row.getCell(9).setCellValue(scope);
                
                row.getCell(10).setCellValue(isUserInput(sourceCode) ? "〇" : "-");

                intStep++;
            }
        }
    }
    
    private final static String[] INPUT_TAGS = {"<input", "<select", "<option", "<textarea"};
    private final static String[] ESCAPE_HTML_KEY = {"(><:=)(.*?)(?>)"};
    private final static String[] ESCAPE_HTMLATTR_KEY = {};
    private final static String[] ESCAPE_JS_KEY = {"onclick="};
    String pattern = "([~accountid:)(.*?)(])";
    private static boolean isUserInput(String sourceCode) {
        for (String item : INPUT_TAGS) {
            if (sourceCode.contains(item)) {
                if (!(item.equals(INPUT_TAGS[0]) && sourceCode.contains("type=\"hidden\""))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private static boolean isEscapeHtmlSource(String sourceCode) {
        for (String item : ESCAPE_HTML_KEY) {
            if (Pattern.matches(item, sourceCode)) {
                return true;
            }
        }
        return false;
    }
    

    private static String getSourceCodeFilePath(String path) {
        return path.replace("/data/wwwroot/m245.local.com/app", "").replace("/", "\\");
    }

    private static String readSourceCodeLine(File file, int pos) throws IOException {
        String st;
        int intStep = 0;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br
                    = new BufferedReader(isr);

            while ((st = br.readLine()) != null) {
                intStep++;
                if (intStep == pos) {
                    return st;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + getSourceCodeFilePath(file.getPath()));
        }
        return null;
    }
}
