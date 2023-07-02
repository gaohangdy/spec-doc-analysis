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
public class SecurityLog {
    private String file;
    private String module;
    private List<SecurityLogItem> items;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<SecurityLogItem> getItems() {
        return items;
    }

    public void setItems(List<SecurityLogItem> items) {
        this.items = items;
    }
    
}
