package com.permissions.dsl;

/**
 * 이진 표현식 정의
 * "fieldName operator value" 형태의 조건을 표현
 * 
 * @param fieldName 비교할 필드명 ("entity.field" 형식)
 * @param operator 연산자 (EQUALS, NOT_EQUALS, etc.)
 * @param value 비교할 값 (직접 값 또는 FieldReference)
 */
public record BinaryExpressionDef(
    String fieldName,
    OperatorType operator,
    Object value  // Value 또는 FieldReference
) implements ExpressionDef {}