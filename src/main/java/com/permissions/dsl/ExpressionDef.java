package com.permissions.dsl;

/**
 * DSL 표현식의 최상위 sealed interface
 * Java 21의 sealed class 패턴을 활용하여 허용되는 표현식 타입을 제한
 */
public sealed interface ExpressionDef 
    permits BinaryExpressionDef, AndExpressionDef, OrExpressionDef, NotExpressionDef {
}