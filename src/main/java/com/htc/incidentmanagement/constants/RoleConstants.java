package com.htc.incidentmanagement.constants;

import java.util.Set;

public final class RoleConstants {

    private RoleConstants() {
    }

    public static final String ADMIN = "ADMIN";
    public static final String MANAGER = "MANAGER";
    public static final String AGENT = "AGENT";
    public static final String USER = "USER";

    public static final Set<String> ALLOWED_ROLES = Set.of(
            ADMIN,
            MANAGER,
            AGENT,
            USER);

}
