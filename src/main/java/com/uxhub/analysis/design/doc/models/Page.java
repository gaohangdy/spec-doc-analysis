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
public class Page {

    private String pageId;
    private String pageName;
    private int methodCnt;
    private int paramterCnt;
    private int partsCnt;
    private int elementCnt;
    private int controllerCnt;

    private String fileName;
    private String filePath;
    private String scName;

    private List<Part> partList;

    private List<PartDetail> elementList;

    private List<CommonLogic> commonLogics;

    public Page() {

    }

    public Page(String fileName, String filePath, String scName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.scName = scName;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public int getMethodCnt() {
        return methodCnt;
    }

    public void setMethodCnt(int methodCnt) {
        this.methodCnt = methodCnt;
    }

    public int getPartsCnt() {
        return partsCnt;
    }

    public void setPartsCnt(int partsCnt) {
        this.partsCnt = partsCnt;
    }

    public int getControllerCnt() {
        return controllerCnt;
    }

    public void setControllerCnt(int controllerCnt) {
        this.controllerCnt = controllerCnt;
    }

    public int getParamterCnt() {
        return paramterCnt;
    }

    public void setParamterCnt(int paramterCnt) {
        this.paramterCnt = paramterCnt;
    }

    public int getElementCnt() {
        return elementCnt;
    }

    public void setElementCnt(int elementCnt) {
        this.elementCnt = elementCnt;
    }

    public List<Part> getPartList() {
        if (partList == null) {
            partList = new ArrayList<>();
        }
        return partList;
    }

    public void setPartList(List<Part> partList) {
        this.partList = partList;
    }

    public List<PartDetail> getElementList() {
        if (elementList == null) {
            elementList = new ArrayList<>();
        }
        return elementList;
    }

    public void setElementList(List<PartDetail> elementList) {
        this.elementList = elementList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getScName() {
        return scName;
    }

    public void setScName(String scName) {
        this.scName = scName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<CommonLogic> getCommonLogics() {
        if (commonLogics == null) {
            commonLogics = new ArrayList<>();
        }
        return commonLogics;
    }

    public void setCommonLogics(List<CommonLogic> commonLogics) {
        this.commonLogics = commonLogics;
    }

}
