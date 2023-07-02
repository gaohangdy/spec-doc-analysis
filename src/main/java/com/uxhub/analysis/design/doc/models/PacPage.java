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
public class PacPage {

    private String pageId;
    private String pageName;

    private String fileName;
    private String filePath;
    private String scName;

    private String layoutType;
    private String customType;
    private String layoutName;

    private List<PacParameter> parameters;

    private List<PacPart> parts;
    
    private List<PacElement> elements;
    
    private List<String> apis;
    
    private List<String> errors;
    
    private boolean common;

    public PacPage() {

    }

    public PacPage(String fileName, String filePath, String scName) {
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

    public List<PacParameter> getParameters() {
        if (this.parameters == null) {
            this.parameters = new ArrayList<>();
        }
        return parameters;
    }

    public void setParameters(List<PacParameter> parameters) {
        this.parameters = parameters;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public List<PacPart> getParts() {
        if (this.parts == null) {
            this.parts = new ArrayList<>();
        }
        return parts;
    }

    public void setParts(List<PacPart> parts) {
        this.parts = parts;
    }

    public List<PacElement> getElements() {
        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }        
        return elements;
    }

    public void setElements(List<PacElement> elements) {
        this.elements = elements;
    }

    public List<String> getApis() {
        if (this.apis == null) {
            this.apis = new ArrayList<>();
        }
        return apis;
    }

    public void setApis(List<String> apis) {
        this.apis = apis;
    }

    public List<String> getErrors() {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon(boolean common) {
        this.common = common;
    }

}
