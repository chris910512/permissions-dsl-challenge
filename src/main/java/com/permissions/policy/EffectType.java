package com.permissions.policy;

/**
 * 정책 효과 타입을 정의하는 열거형
 * DENY 정책이 ALLOW 정책보다 우선순위가 높음
 */
public enum EffectType {
    ALLOW,
    DENY
}