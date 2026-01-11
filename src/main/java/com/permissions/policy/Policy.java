package com.permissions.policy;

import com.permissions.model.PermissionType;
import com.permissions.dsl.ExpressionDef;
import java.util.Set;

/**
 * 권한 정책을 정의하는 Record
 * 
 * @param name 정책 식별자
 * @param description 정책 설명
 * @param effect 정책 효과 (ALLOW 또는 DENY)
 * @param permissions 적용되는 권한들
 * @param applyFilter 정책 적용 조건을 정의하는 표현식
 * @param requiredData 정책 평가에 필요한 데이터 테이블 목록
 */
public record Policy(
    String name,
    String description,
    EffectType effect,
    Set<PermissionType> permissions,
    ExpressionDef applyFilter,
    Set<String> requiredData
) {}