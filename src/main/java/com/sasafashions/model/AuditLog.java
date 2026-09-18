package com.sasafashions.model;

import java.time.LocalDateTime;

/**
 * Represents an auditable action performed in the system.
 *
 * @author SASA Group
 * @version 1.0
 */
public class AuditLog {

    private long auditId;
    private String userId;
    private String actionType;
    private String tableName;
    private String recordId;
    private String description;
    private LocalDateTime actionTime;

    /**
     * Creates an empty audit-log object.
     */
    public AuditLog() {
    }

    /**
     * Creates a complete audit-log object.
     *
     * @param auditId audit identifier
     * @param userId user who performed the action
     * @param actionType type of action
     * @param tableName affected table
     * @param recordId affected record
     * @param description action description
     * @param actionTime date and time of action
     */
    public AuditLog(
            long auditId,
            String userId,
            String actionType,
            String tableName,
            String recordId,
            String description,
            LocalDateTime actionTime
    ) {
        this.auditId = auditId;
        this.userId = userId;
        this.actionType = actionType;
        this.tableName = tableName;
        this.recordId = recordId;
        this.description = description;
        this.actionTime = actionTime;
    }

    public long getAuditId() {
        return auditId;
    }

    public void setAuditId(long auditId) {
        this.auditId = auditId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(
            String actionType
    ) {
        this.actionType = actionType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(
            String tableName
    ) {
        this.tableName = tableName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(
            String recordId
    ) {
        this.recordId = recordId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(
            LocalDateTime actionTime
    ) {
        this.actionTime = actionTime;
    }
}