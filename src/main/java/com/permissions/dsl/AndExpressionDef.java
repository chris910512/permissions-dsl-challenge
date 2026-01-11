package com.permissions.dsl;

import java.util.List;

/**
 * AND 논리곱 표현식 정의
 * 모든 하위 표현식이 참이어야 전체가 참
 * 
 * @param expressions AND로 연결될 표현식들의 리스트
 */
public record AndExpressionDef(List<ExpressionDef> expressions) implements ExpressionDef {}