package com.permissions.dsl;

/**
 * NOT 부정 표현식 정의
 * 하위 표현식의 결과를 반전
 * 
 * @param expression 부정할 표현식
 */
public record NotExpressionDef(ExpressionDef expression) implements ExpressionDef {}