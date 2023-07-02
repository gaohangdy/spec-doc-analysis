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
public class PacPart {

    private String partId;
    private String partName;
    private String partNo;
    private List<PacParameter> parametersPage;
    private List<PacParameter> parametersPart;
    private String description;

    private String scName;
    private String fileName;
    private String filePath;

    private List<String> apis;

    private List<String> errors;
    
    private boolean notSubPart;

    public boolean isNotSubPart() {
        return notSubPart;
    }

    public void setNotSubPart(boolean notSubPart) {
        this.notSubPart = notSubPart;
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

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public List<PacParameter> getParametersPage() {
        if (this.parametersPage == null) {
            this.parametersPage = new ArrayList<>();
        }
        return parametersPage;
    }

    public void setParametersPage(List<PacParameter> parametersPage) {
        this.parametersPage = parametersPage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<PacParameter> getParametersPart() {
        if (this.parametersPart == null) {
            this.parametersPart = new ArrayList<>();
        }
        return parametersPart;
    }

    public void setParametersPart(List<PacParameter> parametersPart) {
        this.parametersPart = parametersPart;
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
}
