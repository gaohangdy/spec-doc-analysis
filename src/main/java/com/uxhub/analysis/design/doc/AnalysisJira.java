/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc;

import com.opencsv.CSVReader;
import com.uxhub.analysis.design.doc.models.Ticket;
import com.uxhub.analysis.design.doc.models.User;
import com.uxhub.analysis.design.doc.utils.ReadUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gaohang
 */
public class AnalysisJira {

    private final static String FILE_PATH = "C:\\work\\JIRA.csv";
    private final static String OUTPUT_FILE_PATH = "C:\\work";
//    private final static String FILE_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\03_開発・単体テスト\\temp\\サーバサイドの入力項目チェック\\ログファイルExcel化";
    private final static String OUTPUT_FILE_TEMPLATE = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\分析用テンプレート\\JIRA_Relation_Pac_template.xlsx";
    private final static String OUTPUT_FILE_PREFIX = "JIRA_Relation_Pac";
    private final static int START_ROW_NUM = 2;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        readCsvData(FILE_PATH);
    }

    private static void readCsvData(String filePath) {
        try ( CSVReader reader = new CSVReader(new FileReader(filePath, StandardCharsets.UTF_8), ',')) {
            List<Ticket> pacTickets = new ArrayList<>();

            List<User> pacUsers = getPacUsers();

            boolean isMatched = false;
            // read line by line
            String[] record;
            boolean isHeader = true;
            while ((record = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Ticket pacTicket = new Ticket();

                for (int intStep = 0; intStep < record.length - 258; intStep++) {
                    if (!"".equals(record[intStep + 258])) {
                        System.out.println("Col: " + (intStep + 258));
                        isMatched = isPacRelation(record[intStep + 258], pacUsers, pacTicket);
                        if (isMatched) {
                            break;
                        }
                    }

                }

                if (isMatched) {
                    pacTicket.setCode(record[1]);
                    pacTicket.setType(record[3]);
                    pacTicket.setStatus(record[4]);
                    pacTicket.setCreateDate(record[19]);
                    pacTicket.setReportor(record[15]);
                    
                    pacTicket.setCauseType(record[257]);
                    pacTicket.setCauseByPhase(record[255]);

                    pacTickets.add(pacTicket);
                }
                System.out.println(record);
            }

            writeExcel(pacTickets);
            System.out.println("abc");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isPacRelation(String comment, List<User> pacUsers, Ticket pacTicket) {
        String[] lines = comment.split("\n");
        String pattern = "([~accountid:)(.*?)(])";

        int intStep = 0;
        for (String line : lines) {
            if (intStep == 0) {
                System.out.println("comment first Line: " + line);
                String[] users = line.split(";");
//                System.out.println("コメント日時：" + users[0]);
//                System.out.println("コメントユーザ：" + users[1]);
//                System.out.println("@ユーザ：" + users[2]);

                if (users.length > 1 && isPacUser(users[1], pacUsers, pacTicket)) {
                    System.out.println("Find Pac User from comment user.");
                    return true;
                }

                if (users.length < 3) {
                    continue;
                }
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(users[2]);

                if (m.find()) {
                    String[] atUsers = users[2].replaceAll("(\\[~accountid:)+|(\\])", "").split(" ");

                    for (String item : atUsers) {
                        if (isPacUser(item, pacUsers, pacTicket)) {
                            System.out.println("Find Pac User from @ user.");
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean isPacUser(String userId, List<User> pacUsers, Ticket pacTicket) {
        Optional<User> partMatch = pacUsers.stream().filter(e -> e.getId().equals(userId)).findFirst();

        if (partMatch.isPresent()) {
            System.out.println("Find User: " + partMatch.get().getName());
            pacTicket.setFirstMatchedUser(partMatch.get().getName());
            return true;
        }
        return false;
    }

    private static void writeExcel(List<Ticket> pacTickets) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(new File(OUTPUT_FILE_TEMPLATE));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
//            for (Ticket pacTicket : pacTickets) {
            Sheet sheet = workbook.getSheet("List");
            writeDatas(sheet, pacTickets);
//            }

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(OUTPUT_FILE_PATH + "\\" + OUTPUT_FILE_PREFIX + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();
    }

    private static void writeDatas(Sheet sheet, List<Ticket> logs) {
        int intStep = 0;
        System.out.println("Sheet: " + sheet.getSheetName());
        for (Ticket item : logs) {

            Row row = sheet.getRow(START_ROW_NUM + intStep);
            System.out.println("Line:" + intStep);

            row.getCell(1).setCellValue(intStep + 1);
            row.getCell(2).setCellValue(item.getCode());
            row.getCell(3).setCellValue(item.getType());
            row.getCell(4).setCellValue(item.getStatus());
            row.getCell(5).setCellValue(item.getCreateDate());
            row.getCell(6).setCellValue(item.getReportor());
            row.getCell(7).setCellValue(item.getFirstMatchedUser());
            
            row.getCell(8).setCellValue(item.getCauseType());
            row.getCell(9).setCellValue(item.getCauseByPhase());

            intStep++;

        }
    }

//    private static void downloadTicketDetail() throws MalformedURLException, IOException {
////        URL url = new URL("https://nri-digital.atlassian.net/si/jira.issueviews:issue-xml/MKKC-1215/MKKC-1215.xml");
////
////        URLConnection urlc = url.openConnection();
////        urlc.setDoOutput(true);
////        WritableByteChannel rbc = Channels.newChannel(urlc.getOutputStream());
//        
////        System.out.println(rbc);
//
////        HttpURLConnection http = (HttpURLConnection) url.openConnection();
////        http.setDoInput(true);
////        http.setRequestProperty("Authorization", "Basic bG9naW46cGFzc3dvcmQ=");
////
////        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
////        http.disconnect();
//    }

    private static List<User> getPacUsers() {
        List<User> pacUsers = new ArrayList<>();
        User pacUser = new User();
        pacUser.setId("627c74e719b129006829dfe6");
        pacUser.setName("Sato Kosuke / 佐藤孝介[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5ef9847225cebd0bb6ad052e");
        pacUser.setName("[PAC] 畢翠華");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5a7119c86cd768507cc235f1");
        pacUser.setName("xin.jin@pactera.com");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("557058:d5003706-a5a5-4235-a886-5281d661c269");
        pacUser.setName("Mu Lin/牟 林[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("630dd2988473817d7d041d04");
        pacUser.setName("李香伶 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("62c638face5a604dbfb4e775");
        pacUser.setName("ba bingbing / 巴氷氷 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5ba04b801e436030d5c4ad82");
        pacUser.setName("Chen Zeguang / 陳澤広[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("6172245e327da40069d4970a");
        pacUser.setName("Han Xiaoming / 韓暁明【PAC】");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5ba04b8176d5bf0ce579dfad");
        pacUser.setName("zhao jiuyu / 趙久宇 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("557058:c7081704-c92a-4d21-8a62-6b6b6fe181cc");
        pacUser.setName("liu guangyi / 劉 光義 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5d25d94ff42dce0c24c6adca");
        pacUser.setName("yan xiuli / 閻 秀麗[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("6113890a97981000700e4a0f");
        pacUser.setName("zhao xiu / 趙 秀 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("64266b399796ea0a8719e0ae");
        pacUser.setName("Wang Ruichao / 王 瑞超 [PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5e450c2b90dfb70c9e60b91a");
        pacUser.setName("Wu yuerong / 呉 躍栄【PAC】");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("63a94ff815d69a40aa1852f4");
        pacUser.setName("楊臣[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("5ba473fc653ec8726d2a008a");
        pacUser.setName("mitsuo.akashi@pactera.com");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("634fa61476b91b62562bbd14");
        pacUser.setName("馬少卿[PAC]");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("621c72cfc88f1000682d2395");
        pacUser.setName("姜新");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("63a95104030d706ab0e3bdd9");
        pacUser.setName("劉毅恒");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("62e22599b6b0b70770d849fc");
        pacUser.setName("曹暁帆");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("6308185d52aa1a8eaab65edd");
        pacUser.setName("高原");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("61fb60460d3777006a91c052");
        pacUser.setName("井関亨");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("61a53acffe9f30006894719a");
        pacUser.setName("曾慶栄");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("620c50c259709300698e66fa");
        pacUser.setName("楼 旭紅");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("59f9836d87b3f8190d7dc347");
        pacUser.setName("衡晶");
        pacUsers.add(pacUser);
        pacUser = new User();
        pacUser.setId("63a95286b790087ed7102fbf");
        pacUser.setName("崔徳生");

        return pacUsers;
    }
}
