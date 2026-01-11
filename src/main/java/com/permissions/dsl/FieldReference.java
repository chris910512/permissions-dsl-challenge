package com.permissions.dsl;

/**
 * 필드 참조를 위한 Record
 * 다른 엔티티의 필드를 참조할 때 사용
 * 
 * @param fieldName "entity.field" 형식의 필드명
 */
public record FieldReference(String fieldName) {}