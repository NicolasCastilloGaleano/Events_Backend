package com.events.security.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document()
public class Role {
    @Id
    private String id;
    private String name;
    private String description;
    private int status;
    @DBRef
    private Permission[] totalPermissions;

    public Role() {
    }

    public Role(String name, String description, Permission[] totalPermissions) {
        this.name = name;
        this.description = description;
        this.totalPermissions = totalPermissions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Permission[] getTotalPermissions() {
        return totalPermissions;
    }

    public void setTotalPermissions(Permission[] totalPermissions) {
        this.totalPermissions = totalPermissions;
    }

    public void addPermission(Permission permission) {
        if (totalPermissions == null) {
            totalPermissions = new Permission[1];
            totalPermissions[0] = permission;
        } else {
            // Crea un nuevo array con una longitud mayor para agregar el nuevo permiso
            Permission[] newPermissions = new Permission[totalPermissions.length + 1];

            // Copia los permisos existentes al nuevo array
            System.arraycopy(totalPermissions, 0, newPermissions, 0, totalPermissions.length);

            // Agrega el nuevo permiso al final del nuevo array
            newPermissions[totalPermissions.length] = permission;

            // Asigna el nuevo array a totalPermissions
            totalPermissions = newPermissions;
        }
    }

    public void removePermission(Permission permissionToRemove) {
        if (totalPermissions == null) {
            return;
        }

        List<Permission> permissionList = new ArrayList<>(Arrays.asList(totalPermissions));

        if (permissionList.remove(permissionToRemove)) {
            totalPermissions = permissionList.toArray(new Permission[0]);
        }
    }
}
