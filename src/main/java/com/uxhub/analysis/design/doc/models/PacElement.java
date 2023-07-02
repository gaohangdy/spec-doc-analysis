/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uxhub.analysis.design.doc.models;

/**
 *
 * @author gaohang
 */
public class PacElement {

    private String elementNo;
    private String elementId;
    private String dispCondition;
    private boolean i18nFlg;
    private String description;
    private boolean hiddenFlg;

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getDispCondition() {
        return dispCondition;
    }

    public void setDispCondition(String dispCondition) {
        this.dispCondition = dispCondition;
    }

    public boolean isI18nFlg() {
        return i18nFlg;
    }

    public void setI18nFlg(boolean i18nFlg) {
        this.i18nFlg = i18nFlg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return hiddenFlg;
    }

    public void setHidden(boolean hidden) {
        this.hiddenFlg = hidden;
    }

}
