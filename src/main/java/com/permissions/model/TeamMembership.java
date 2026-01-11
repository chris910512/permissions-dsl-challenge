package com.permissions.model;

/**
 * 팀 멤버십 관계 엔티티
 * 사용자와 팀 간의 멤버십 관계와 역할을 정의
 * 
 * @param userId 사용자 ID
 * @param teamId 팀 ID
 * @param role 팀 내 역할 (VIEWER, EDITOR, ADMIN)
 */
public record TeamMembership(String userId, String teamId, RoleType role) {}