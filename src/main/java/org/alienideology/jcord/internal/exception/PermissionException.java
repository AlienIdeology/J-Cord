package org.alienideology.jcord.internal.exception;

import org.alienideology.jcord.handle.Permission;

import java.util.Arrays;

/**
 * @author AlienIdeology
 */
public class PermissionException extends RuntimeException {

    private Permission[] permission;

    public PermissionException(Permission... permission) {
        super("Missing permission(s): " + Arrays.toString(permission));
        this.permission = permission;
    }

    public PermissionException(String cause) {
        super(cause);
        this.permission = null;
    }

    public Permission[] getMissingPermissions() {
        return permission;
    }
}
