/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.models;

/**
 *
 * @author gaohang
 */
public class CommonLogic {
    private String logicId;
    private String logicName;
    
    private boolean pageScope;

    public String getLogicId() {
        return logicId;
    }

    public void setLogicId(String logicId) {
        this.logicId = logicId;
    }

    public String getLogicName() {
        return logicName;
    }

    public void setLogicName(String logicName) {
        this.logicName = logicName;
    }

    public boolean isPageScope() {
        return pageScope;
    }

    public void setPageScope(boolean pageScope) {
        this.pageScope = pageScope;
    }
    
}
