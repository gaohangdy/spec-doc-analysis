/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.utils;

import com.uxhub.analysis.design.doc.AnalysisDesignDoc;
import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.FOLDER_NAME_PAGE;
import static com.uxhub.analysis.design.doc.AnalysisDesignDoc.OUTPUT_PATH;
import com.uxhub.analysis.design.doc.models.PacPage;
import com.uxhub.analysis.design.doc.models.PacPart;
import com.uxhub.analysis.design.doc.models.Page;
import com.uxhub.analysis.design.doc.models.Part;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gaohang
 */
public class RelateIODoc {

    private static final String ROOT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\01_ input\\20221031_静止点\\060.フロント画面設計\\";
    private final static String DOC_ROOT_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\02_Design\\02_詳設\\";
    private final static String[] FOLDERS = {"02_画面組込む", "01_バーツ"};

    private final static String TEMPATE_PATH = "C:\\Users\\gaohang\\Pactera\\MKC_EC_再構築 - Documents\\Personal\\gaohang\\リソース計画\\分析用テンプレート\\";
    private final static String TEMPLATE_NAME = "内部設計_作業タスク一覧_template.xlsx";
    private final static String OUTPUT_FILE_PREFIX = "内部設計_作業タスク一覧";

    private List<Page> inputPages;
    private List<Part> inputParts;

    public void execute() throws IOException {
        List<PacPage> pages = this.relate();

        this.writeToReport(pages);
    }

    private List<PacPage> relate() {

        getInputInfo();

        List<PacPage> relatedPages = new ArrayList<>();

        ReadPacDoc readPacDoc = new ReadPacDoc();
        List<PacPage> outputPages = readPacDoc.getPacPages();

        List<PacPart> outputPartFiles = new ArrayList<>();
        ReadUtils.getOutputPartDocs(DOC_ROOT_PATH + FOLDERS[1], outputPartFiles);

        for (Page inputPage : this.inputPages) {
            PacPage outputPage = new PacPage();

            Optional<PacPage> outputPageMatch = outputPages.stream().filter(e -> e.getPageId().equals(inputPage.getPageId())).findFirst();
            // 外部設計書の画面IDで内部設計書を作成かどうか？
            if (outputPageMatch.isPresent()) {
                outputPage = outputPageMatch.get();

                if (outputPage.getPageName() == null) {
                    outputPage.setPageName(inputPage.getPageName());
                    outputPage.getErrors().add("内部設計書で画面名記入で標準化違反");
                } else if (!inputPage.getPageName().equals(outputPage.getPageName())) {
                    outputPage.getErrors().add("内部設計書に記入画面名と外部設計書の記載が不一致");
                }
                this.setOutputPartNames(outputPage.getParts(), outputPartFiles);

                for (Part inputPart : inputPage.getPartList()) {
                    if ("MCKNP0720".equals(inputPart.getPartId())) {
                        System.out.println("Stop MCKNP0180");
                    }
                    Optional<PacPart> outputPartMatch = outputPage.getParts().stream().filter(e -> e.getPartId().equals(inputPart.getPartId())).findFirst();
                    if (outputPartMatch.isPresent()) {
                        outputPartMatch.get().setPartName(inputPart.getPartName());
                        Optional<Part> outputPartFileMatch = this.inputParts.stream().filter(e -> e.getPartId().equals(outputPartMatch.get().getPartId())).findFirst();
                        if (outputPartFileMatch.isPresent()) {
                            outputPartFileMatch.get().setNotSubPart(true);
                        }
                    } else if (!"P-0001".equals(inputPart.getPartId()) && !"P-0002".equals(inputPart.getPartId()) && !"P-0003".equals(inputPart.getPartId()) && !"MCACP0010".equals(inputPart.getPartId()) && !"MCACP0020".equals(inputPart.getPartId()) && !"MCACP0030".equals(inputPart.getPartId())) {
                        PacPart noMatchPart = new PacPart();
                        noMatchPart.setPartId(inputPart.getPartId());
                        noMatchPart.setPartName(inputPart.getPartName());
                        noMatchPart.getErrors().add("・ 内部設計の画面組込でパーツを記載しない");

                        outputPage.getParts().add(noMatchPart);
                    }
                }
            } else {
                outputPage.setPageId(inputPage.getPageId());
                outputPage.setPageName(inputPage.getPageName());
                outputPage.getErrors().add("・ 内部設計の「画面組込設計書」なし");
            }

            relatedPages.add(outputPage);
        }

        // 子パーツの情報
        List<Part> subParts = this.inputParts.stream().filter(e -> !e.isNotSubPart()).collect(Collectors.toList());
        PacPage commonPage = new PacPage();
        commonPage.setPageId("MCACG0000");
        commonPage.setPageName("子パーツ");
        commonPage.setCommon(true);
        for (Part part : subParts) {
            PacPart outputPart = new PacPart();
            Optional<PacPart> outputPartFileMatch = outputPartFiles.stream().filter(e -> e.getPartId().equals(part.getPartId())).findFirst();
            if (outputPartFileMatch.isPresent()) {
                outputPart = outputPartFileMatch.get();
                readPacDoc.getPartInfo(outputPart);
                commonPage.getParts().add(outputPart);
            } else {
                outputPart.setPartId(part.getPartId());
                outputPart.setPartName(part.getPartName());
                outputPart.getErrors().add("・ 内部設計の「パーツ設計書」なし");
            }

        }
        relatedPages.add(commonPage);

        return relatedPages;
    }

    private void writeToReport(List<PacPage> pages) throws IOException {
//        pages.sort(Comparator.comparing(PacPage::getPageId));

        FileInputStream inputStream = new FileInputStream(new File(TEMPATE_PATH + TEMPLATE_NAME));
        FileOutputStream outputStream;
        try ( Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet("作業タスク一覧");
            WriteUtils.writeSheetDatas(sheet, pages, false);

            String pattern = "yyyyMMddHHmmss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            outputStream = new FileOutputStream(OUTPUT_PATH + "\\export\\" + OUTPUT_FILE_PREFIX + "_" + dateFormat.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
        }
        outputStream.close();

//        
//        for (PacPage page : pages) {
//            String errorPage = page.getErrors().isEmpty() ? "" : ", [Error: " + page.getErrors().size() + "]";
//            System.out.println("画面組込[ID: " + page.getPageId() + "], [Name: " + page.getPageName() + "]" + errorPage);
//            for (String api : page.getApis()) {
//                System.out.println("    API: " + api);
//            }
//            for (PacPart part : page.getParts()) {
//                String errorPart = part.getErrors().isEmpty() ? "" : ", [Error: " + part.getErrors().size() + "]";
//                System.out.println("    パーツ[ID: " + part.getPartId() + "], [Name: " + part.getPartName() + "]" + errorPart);
//                for (String api : part.getApis()) {
//                    System.out.println("        API: " + api);
//                }
//            }
//        }
    }

    private void getInputInfo() {
        List<Page> pageList = new ArrayList<>();
        AnalysisDesignDoc.getPageDocs(ROOT_PATH + FOLDER_NAME_PAGE, pageList, "");
        try {
            for (Page page : pageList) {
                ReadPageInfo.readFile(page);
            }

            ReadPageInfo.getCommonParts();

            this.inputPages = pageList;
            this.inputParts = ReadPageInfo.partList;
        } catch (IOException ex) {
            Logger.getLogger(AnalysisDesignDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setOutputPartNames(List<PacPart> outputPart, List<PacPart> outputPartFiles) {
        for (PacPart part : outputPart) {
            Optional<PacPart> outputPartFileMatch = outputPartFiles.stream().filter(e -> e.getPartId().equals(part.getPartId())).findFirst();
            if (outputPartFileMatch.isPresent()) {
                part.setPartName(outputPartFileMatch.get().getPartName());
            } else {
                part.getErrors().add("・ 内部設計の「パーツ設計書」なし(内部設計のパーツ設計書ファイル配列から)");
            }
        }
    }
}
