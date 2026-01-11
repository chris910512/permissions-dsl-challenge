package com.permissions.dsl;

/**
 * 이진 표현식에서 사용되는 연산자 타입
 * 관계형 데이터베이스의 WHERE 절과 유사한 연산자들을 제공
 */
public enum OperatorType {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_EQUAL,
    LESS_EQUAL
}