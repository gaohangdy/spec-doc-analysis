/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.models;

import java.util.List;

/**
 *
 * @author gaohang
 */
public class LogFile {

    private String fileName;
    private String sheetName;
    private List<SecurityLog> logs;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<SecurityLog> getLogs() {
        return logs;
    }

    public void setLogs(List<SecurityLog> logs) {
        this.logs = logs;
    }

}
