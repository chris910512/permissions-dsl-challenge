package com.permissions.evaluator;

import com.permissions.dsl.*;
import java.util.Map;
import java.util.Objects;

/**
 * Expression DSL 평가 엔진
 * 3-value 논리(TRUE/FALSE/NULL)와 단락 평가를 지원
 * Figma DSL 패턴을 참고하여 데이터 로딩과 평가 로직을 분리
 */
public class ExpressionEvaluator {

    /**
     * 표현식을 평가하여 결과를 반환
     * 
     * @param expression 평가할 표현식
     * @param data 엔티티별 데이터 맵 ("user" -> {"id": "u1", "name": "User1"})
     * @return 평가 결과 (TRUE/FALSE/NULL)
     */
    public EvaluationResult evaluate(ExpressionDef expression, Map<String, Map<String, Object>> data) {
        return switch (expression) {
            case BinaryExpressionDef binary -> evaluateBinary(binary, data);
            case AndExpressionDef and -> evaluateAnd(and, data);
            case OrExpressionDef or -> evaluateOr(or, data);
            case NotExpressionDef not -> evaluateNot(not, data);
        };
    }

    /**
     * 이진 표현식 평가
     */
    private EvaluationResult evaluateBinary(BinaryExpressionDef binary, Map<String, Map<String, Object>> data) {
        // 필드값 추출
        Object fieldValue = getFieldValue(binary.fieldName(), data);
        if (fieldValue == null && !data.containsKey(getTableName(binary.fieldName()))) {
            return EvaluationResult.NULL; // 테이블 자체가 로드되지 않음
        }

        // 비교값 추출
        Object compareValue = binary.value();
        if (compareValue instanceof FieldReference fieldRef) {
            compareValue = getFieldValue(fieldRef.fieldName(), data);
            if (compareValue == null && !data.containsKey(getTableName(fieldRef.fieldName()))) {
                return EvaluationResult.NULL; // 참조된 테이블이 로드되지 않음
            }
        }

        // 값 비교
        return compareValues(fieldValue, binary.operator(), compareValue);
    }

    /**
     * AND 표현식 평가 (단락 평가)
     */
    private EvaluationResult evaluateAnd(AndExpressionDef and, Map<String, Map<String, Object>> data) {
        boolean hasNull = false;
        
        for (ExpressionDef expr : and.expressions()) {
            EvaluationResult result = evaluate(expr, data);
            
            switch (result) {
                case FALSE -> {
                    return EvaluationResult.FALSE; // 단락 평가: 하나라도 FALSE면 즉시 FALSE
                }
                case NULL -> hasNull = true; // NULL은 나중에 처리
                case TRUE -> {} // 계속 진행
            }
        }
        
        return hasNull ? EvaluationResult.NULL : EvaluationResult.TRUE;
    }

    /**
     * OR 표현식 평가 (단락 평가)
     */
    private EvaluationResult evaluateOr(OrExpressionDef or, Map<String, Map<String, Object>> data) {
        boolean hasNull = false;
        
        for (ExpressionDef expr : or.expressions()) {
            EvaluationResult result = evaluate(expr, data);
            
            switch (result) {
                case TRUE -> {
                    return EvaluationResult.TRUE; // 단락 평가: 하나라도 TRUE면 즉시 TRUE
                }
                case NULL -> hasNull = true; // NULL은 나중에 처리
                case FALSE -> {} // 계속 진행
            }
        }
        
        return hasNull ? EvaluationResult.NULL : EvaluationResult.FALSE;
    }

    /**
     * NOT 표현식 평가
     */
    private EvaluationResult evaluateNot(NotExpressionDef not, Map<String, Map<String, Object>> data) {
        EvaluationResult result = evaluate(not.expression(), data);
        
        return switch (result) {
            case TRUE -> EvaluationResult.FALSE;
            case FALSE -> EvaluationResult.TRUE;
            case NULL -> EvaluationResult.NULL; // NULL의 부정도 NULL
        };
    }

    /**
     * 필드명에서 테이블명 추출
     * "user.id" -> "user"
     */
    private String getTableName(String fieldName) {
        int dotIndex = fieldName.indexOf('.');
        return dotIndex > 0 ? fieldName.substring(0, dotIndex) : fieldName;
    }

    /**
     * 필드명에서 컬럼명 추출
     * "user.id" -> "id"
     */
    private String getColumnName(String fieldName) {
        int dotIndex = fieldName.indexOf('.');
        return dotIndex > 0 ? fieldName.substring(dotIndex + 1) : fieldName;
    }

    /**
     * 데이터에서 필드값 추출
     */
    private Object getFieldValue(String fieldName, Map<String, Map<String, Object>> data) {
        String tableName = getTableName(fieldName);
        String columnName = getColumnName(fieldName);
        
        Map<String, Object> tableData = data.get(tableName);
        if (tableData == null) {
            return null; // 테이블이 로드되지 않음
        }
        
        return tableData.get(columnName);
    }

    /**
     * 연산자에 따른 값 비교
     */
    private EvaluationResult compareValues(Object left, OperatorType operator, Object right) {
        // null 처리
        if (left == null || right == null) {
            return switch (operator) {
                case EQUALS -> Objects.equals(left, right) ? EvaluationResult.TRUE : EvaluationResult.FALSE;
                case NOT_EQUALS -> !Objects.equals(left, right) ? EvaluationResult.TRUE : EvaluationResult.FALSE;
                default -> EvaluationResult.NULL; // null과 다른 비교연산은 불가
            };
        }

        // 타입이 다르면 비교 불가
        if (!left.getClass().equals(right.getClass())) {
            return switch (operator) {
                case EQUALS -> EvaluationResult.FALSE;
                case NOT_EQUALS -> EvaluationResult.TRUE;
                default -> EvaluationResult.NULL;
            };
        }

        return switch (operator) {
            case EQUALS -> Objects.equals(left, right) ? EvaluationResult.TRUE : EvaluationResult.FALSE;
            case NOT_EQUALS -> !Objects.equals(left, right) ? EvaluationResult.TRUE : EvaluationResult.FALSE;
            case GREATER_THAN -> compareComparable(left, right) > 0 ? EvaluationResult.TRUE : EvaluationResult.FALSE;
            case LESS_THAN -> compareComparable(left, right) < 0 ? EvaluationResult.TRUE : EvaluationResult.FALSE;
            case GREATER_EQUAL -> compareComparable(left, right) >= 0 ? EvaluationResult.TRUE : EvaluationResult.FALSE;
            case LESS_EQUAL -> compareComparable(left, right) <= 0 ? EvaluationResult.TRUE : EvaluationResult.FALSE;
        };
    }

    /**
     * Comparable 객체 비교
     */
    @SuppressWarnings("unchecked")
    private int compareComparable(Object left, Object right) {
        if (!(left instanceof Comparable leftComp) || !(right instanceof Comparable rightComp)) {
            throw new IllegalArgumentException("Cannot compare non-Comparable objects: " + 
                left.getClass() + " and " + right.getClass());
        }
        return leftComp.compareTo(rightComp);
    }
}