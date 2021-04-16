package com.scpfoundation.psybotic.disastercheckservice.Models;

import java.util.Date;
import java.util.Objects;

public class Notification {
    private String id;
    private String userId;
    private String textHeader;
    private String text;
    private boolean status;
    private boolean reply;
    private Date sendingDate;
    private Date replyDate;
    private boolean bildiri;

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", textHeader='" + textHeader + '\'' +
                ", text='" + text + '\'' +
                ", status=" + status +
                ", reply=" + reply +
                ", sendingDate=" + sendingDate +
                ", replyDate=" + replyDate +
                ", bildiri=" + bildiri +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTextHeader() {
        return textHeader;
    }

    public void setTextHeader(String textHeader) {
        this.textHeader = textHeader;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isBildiri() {
        return bildiri;
    }

    public void setBildiri(boolean bildiri) {
        this.bildiri = bildiri;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return isStatus() == that.isStatus() && isReply() == that.isReply() && Objects.equals(getId(), that.getId()) && Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getTextHeader(), that.getTextHeader()) && Objects.equals(getText(), that.getText()) && Objects.equals(getSendingDate(), that.getSendingDate()) && Objects.equals(getReplyDate(), that.getReplyDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getTextHeader(), getText(), isStatus(), isReply(), getSendingDate(), getReplyDate());
    }

}
