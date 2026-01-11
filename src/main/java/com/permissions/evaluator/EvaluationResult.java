package com.permissions.evaluator;

/**
 * 3-value 논리를 위한 평가 결과 열거형
 * 
 * TRUE: 확정적으로 참
 * FALSE: 확정적으로 거짓
 * NULL: 데이터 부족으로 평가 불가 (지연 로딩 필요)
 */
public enum EvaluationResult {
    TRUE,
    FALSE,
    NULL
}