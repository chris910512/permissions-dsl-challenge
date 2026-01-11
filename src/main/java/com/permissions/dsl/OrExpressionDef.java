package com.permissions.dsl;

import java.util.List;

/**
 * OR 논리합 표현식 정의
 * 하나 이상의 하위 표현식이 참이면 전체가 참
 * 
 * @param expressions OR로 연결될 표현식들의 리스트
 */
public record OrExpressionDef(List<ExpressionDef> expressions) implements ExpressionDef {}