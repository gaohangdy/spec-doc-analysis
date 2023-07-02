/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaohang
 */
public class Part {

    private String partId;
    private String partName;
    // 0: 画面単位で更新 1: 部分更新（常に最新データ取得）2: 部分更新（画面内データ操作）
    private String updateType;
    private int outputCnt;
    private int inputCnt;
    private int staticCnt;
    private int eventCnt;

    private String fileName;
    private String filePath;
    private String scName;

    private List<PartInPage> parentPageList;

    private int validationCnt;

    private int initMethodCnt;

    private boolean commonPart;

    private String sheetName;

    private List<PartElement> elements;

    private boolean notSubPart;

    public Part() {

    }

    public Part(String partId, String partName, String fileName, String filePath, String scName) {
        this.partId = partId;
        this.partName = partName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.scName = scName;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getOutputCnt() {
        return outputCnt;
    }

    public void setOutputCnt(int outputCnt) {
        this.outputCnt = outputCnt;
    }

    public int getInputCnt() {
        return inputCnt;
    }

    public void setInputCnt(int inputCnt) {
        this.inputCnt = inputCnt;
    }

    public int getStaticCnt() {
        return staticCnt;
    }

    public void setStaticCnt(int staticCnt) {
        this.staticCnt = staticCnt;
    }

    public int getEventCnt() {
        return eventCnt;
    }

    public void setEventCnt(int eventCnt) {
        this.eventCnt = eventCnt;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public int getValidationCnt() {
        return validationCnt;
    }

    public void setValidationCnt(int validationCnt) {
        this.validationCnt = validationCnt;
    }

    public int getInitMethodCnt() {
        return initMethodCnt;
    }

    public void setInitMethodCnt(int initMethodCnt) {
        this.initMethodCnt = initMethodCnt;
    }

    public List<PartInPage> getParentPageList() {
        if (parentPageList == null) {
            parentPageList = new ArrayList<>();
        }
        return parentPageList;
    }

    public void setParentPageList(List<PartInPage> parentPageList) {
        this.parentPageList = parentPageList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public boolean isCommonPart() {
        return commonPart;
    }

    public void setCommonPart(boolean commonPart) {
        this.commonPart = commonPart;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<PartElement> getElements() {
        return elements;
    }

    public void setElements(List<PartElement> elements) {
        this.elements = elements;
    }

    public boolean isNotSubPart() {
        return notSubPart;
    }

    public void setNotSubPart(boolean notSubPart) {
        this.notSubPart = notSubPart;
    }

}
