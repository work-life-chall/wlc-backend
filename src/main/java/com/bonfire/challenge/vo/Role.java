package com.bonfire.challenge.vo;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER(1, "일반 사용자"),
    ROLE_ADMIN(2, "관리자"),
    ROLE_SUPER_ADMIN(3, "슈퍼 관리자"),
    ROLE_TEMP_USER(0, "임시 사용자")
    ;

    private final int id;
    private final String description;

    Role(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static Role findBy(int id) {
        for ( Role role : values() ) {
            if (role.id == id)
                return role;
        }
        return null;
    }
}
