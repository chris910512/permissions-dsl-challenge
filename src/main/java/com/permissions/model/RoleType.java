package com.permissions.model;

/**
 * 역할 유형을 정의하는 열거형
 * 계층적 권한: VIEWER < EDITOR < ADMIN
 */
public enum RoleType {
    VIEWER,
    EDITOR,
    ADMIN
}