package com.permissions.model;

/**
 * 권한 유형을 정의하는 열거형
 * 문서에 대한 4가지 기본 권한: 보기, 편집, 삭제, 공유
 */
public enum PermissionType {
    CAN_VIEW,
    CAN_EDIT,
    CAN_DELETE,
    CAN_SHARE
}