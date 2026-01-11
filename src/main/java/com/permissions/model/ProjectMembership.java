package com.permissions.model;

/**
 * 프로젝트 멤버십 관계 엔티티
 * 사용자와 프로젝트 간의 멤버십 관계와 역할을 정의
 * 
 * @param userId 사용자 ID
 * @param projectId 프로젝트 ID
 * @param role 프로젝트 내 역할 (VIEWER, EDITOR, ADMIN)
 */
public record ProjectMembership(String userId, String projectId, RoleType role) {}