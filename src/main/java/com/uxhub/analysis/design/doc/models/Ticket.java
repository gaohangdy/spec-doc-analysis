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
public class Ticket {

    private String code;
    private String type;
    private String status;
    private String reportor;
    private String assignee;
    private String createDate;
    private List<Comment> comments;
    private String firstMatchedUser;
    
    // （直接原因）区分
    private String causeType;
    
    // （直接原因）作り込み工程
    private String causeByPhase;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportor() {
        return reportor;
    }

    public void setReportor(String reportor) {
        this.reportor = reportor;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getFirstMatchedUser() {
        return firstMatchedUser;
    }

    public void setFirstMatchedUser(String firstMatchedUser) {
        this.firstMatchedUser = firstMatchedUser;
    }

    public String getCauseType() {
        return causeType;
    }

    public void setCauseType(String causeType) {
        this.causeType = causeType;
    }

    public String getCauseByPhase() {
        return causeByPhase;
    }

    public void setCauseByPhase(String causeByPhase) {
        this.causeByPhase = causeByPhase;
    }


}
