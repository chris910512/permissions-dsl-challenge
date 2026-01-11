package com.permissions.model;

/**
 * 프로젝트 엔티티
 * 
 * @param id 프로젝트 고유 ID
 * @param name 프로젝트 이름
 * @param teamId 소속 팀 ID
 * @param visibility 프로젝트 가시성 (PRIVATE, PUBLIC)
 */
public record Project(String id, String name, String teamId, VisibilityType visibility) {}