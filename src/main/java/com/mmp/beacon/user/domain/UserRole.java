package com.mmp.beacon.user.domain;

public enum UserRole {

        SUPER_ADMIN(0), // 슈퍼 관리자
        ADMIN(1),       // 관리자
        USER(2);        // 일반 사용자

        private final int roleValue;

        UserRole(int roleValue) {
            this.roleValue = roleValue;
        }

        public int getRoleValue() {
            return roleValue;
        }

        public static UserRole fromRoleValue(int roleValue) {
            for (UserRole role : UserRole.values()) {
                if (role.getRoleValue() == roleValue) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Invalid UserRole value: " + roleValue);
        }
    }
