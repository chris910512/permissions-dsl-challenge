package com.permissions.model;

/**
 * 팀 엔티티
 * 
 * @param id 팀 고유 ID
 * @param name 팀 이름
 * @param plan 팀 플랜 (FREE, PRO, ENTERPRISE)
 */
public record Team(String id, String name, PlanType plan) {}