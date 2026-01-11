package com.permissions.model;

/**
 * 사용자 엔티티
 * 
 * @param id 사용자 고유 ID
 * @param email 사용자 이메일
 * @param name 사용자 이름
 */
public record User(String id, String email, String name) {}