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
public class PartElement {
    /**
     * 項目物理名
     */
    private String id;
    /**
     * 項目名
     */
    private String name;
    
    private String type;
    
    private String ioType;
    
    /**
     * 単項目チェックの配列
     */
    private List<String> validations; 
    
    private String event;
    
    private String description;
    
    private String additional;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIoType() {
        return ioType;
    }

    public void setIoType(String ioType) {
        this.ioType = ioType;
    }

    public List<String> getValidations() {
        if (this.validations == null) {
            this.validations = new ArrayList<>();
        }
        return validations;
    }

    public void setValidations(List<String> validations) {
        this.validations = validations;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
    
    
}
