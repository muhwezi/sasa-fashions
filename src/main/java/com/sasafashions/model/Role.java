package com.sasafashions.model;

/**
 * Represents a system security role.
 *
 * @author SASA Group
 * @version 1.0
 */
public class Role {

    private String roleId;
    private String roleName;
    private String description;

    /**
     * Creates an empty role.
     */
    public Role() {
    }

    /**
     * Creates a complete role.
     *
     * @param roleId unique role identifier
     * @param roleName role name
     * @param description role description
     */
    public Role(
            String roleId,
            String roleName,
            String description
    ) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    /**
     * Returns the role name for combo-box display.
     *
     * @return role name
     */
    @Override
    public String toString() {
        return roleName;
    }
}