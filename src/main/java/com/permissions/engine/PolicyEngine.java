package com.permissions.engine;

import com.permissions.policy.Policy;
import com.permissions.policy.EffectType;
import com.permissions.model.PermissionType;
import com.permissions.loader.DataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.evaluator.EvaluationResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 정책 기반 권한 평가 엔진
 * DENY 우선, ALLOW 후순, 기본 거부 원칙을 따름
 */
public class PolicyEngine {
    private final List<Policy> policies = new ArrayList<>();
    private final DataLoader dataLoader;
    private final ExpressionEvaluator evaluator;
    
    public PolicyEngine(DataLoader dataLoader, ExpressionEvaluator evaluator) {
        this.dataLoader = dataLoader;
        this.evaluator = evaluator;
    }
    
    /**
     * 정책을 엔진에 추가
     */
    public void addPolicy(Policy policy) {
        policies.add(policy);
    }
    
    /**
     * 여러 정책을 한번에 추가
     */
    public void addPolicies(Collection<Policy> policies) {
        this.policies.addAll(policies);
    }
    
    /**
     * 모든 정책 조회
     */
    public List<Policy> getPolicies() {
        return new ArrayList<>(policies);
    }
    
    /**
     * 정책 제거
     */
    public boolean removePolicy(String policyName) {
        return policies.removeIf(policy -> policy.name().equals(policyName));
    }
    
    /**
     * 권한 확인 메인 메서드
     * 
     * @param resource 대상 리소스 (예: Document)
     * @param user 사용자 (예: User) 
     * @param permission 확인할 권한
     * @return 권한이 있으면 true, 없으면 false
     */
    public boolean hasPermission(Object resource, Object user, PermissionType permission) {
        // 1. permission에 해당하는 모든 정책 필터링
        var applicablePolicies = policies.stream()
            .filter(p -> p.permissions().contains(permission))
            .toList();
        
        if (applicablePolicies.isEmpty()) {
            return false; // 해당 권한에 대한 정책이 없으면 기본 거부
        }
            
        // 2. 필요한 데이터 파악 및 로드
        var requiredData = collectRequiredData(applicablePolicies);
        var loadedData = dataLoader.loadData(resource, user, requiredData);
        
        // 3. DENY 정책 평가 (하나라도 true면 거부)
        var denyPolicies = applicablePolicies.stream()
            .filter(p -> p.effect() == EffectType.DENY)
            .toList();
            
        for (var policy : denyPolicies) {
            EvaluationResult result = evaluator.evaluate(policy.applyFilter(), loadedData);
            if (result == EvaluationResult.TRUE) {
                return false; // DENY 정책이 매치되면 즉시 거부
            }
            // NULL인 경우는 데이터 부족이므로 계속 진행
        }
        
        // 4. ALLOW 정책 평가 (하나라도 true면 허용)
        var allowPolicies = applicablePolicies.stream()
            .filter(p -> p.effect() == EffectType.ALLOW)
            .toList();
            
        for (var policy : allowPolicies) {
            EvaluationResult result = evaluator.evaluate(policy.applyFilter(), loadedData);
            if (result == EvaluationResult.TRUE) {
                return true; // ALLOW 정책이 매치되면 허용
            }
            // NULL인 경우는 데이터 부족이므로 계속 진행
        }
        
        // 5. 기본값은 거부
        return false;
    }
    
    /**
     * 적용 가능한 정책들에서 필요한 데이터 테이블 목록을 수집
     */
    private Set<String> collectRequiredData(List<Policy> applicablePolicies) {
        return applicablePolicies.stream()
            .flatMap(policy -> policy.requiredData().stream())
            .collect(Collectors.toSet());
    }
    
    /**
     * 디버깅을 위한 정책 평가 상세 결과
     */
    public PolicyEvaluationResult evaluateWithDetails(Object resource, Object user, PermissionType permission) {
        var applicablePolicies = policies.stream()
            .filter(p -> p.permissions().contains(permission))
            .toList();
        
        if (applicablePolicies.isEmpty()) {
            return new PolicyEvaluationResult(false, "No applicable policies found", Collections.emptyList());
        }
        
        var requiredData = collectRequiredData(applicablePolicies);
        var loadedData = dataLoader.loadData(resource, user, requiredData);
        
        List<PolicyResult> results = new ArrayList<>();
        
        // DENY 정책 평가
        var denyPolicies = applicablePolicies.stream()
            .filter(p -> p.effect() == EffectType.DENY)
            .toList();
            
        for (var policy : denyPolicies) {
            EvaluationResult result = evaluator.evaluate(policy.applyFilter(), loadedData);
            results.add(new PolicyResult(policy.name(), policy.effect(), result));
            
            if (result == EvaluationResult.TRUE) {
                return new PolicyEvaluationResult(false, "DENY policy matched: " + policy.name(), results);
            }
        }
        
        // ALLOW 정책 평가
        var allowPolicies = applicablePolicies.stream()
            .filter(p -> p.effect() == EffectType.ALLOW)
            .toList();
            
        for (var policy : allowPolicies) {
            EvaluationResult result = evaluator.evaluate(policy.applyFilter(), loadedData);
            results.add(new PolicyResult(policy.name(), policy.effect(), result));
            
            if (result == EvaluationResult.TRUE) {
                return new PolicyEvaluationResult(true, "ALLOW policy matched: " + policy.name(), results);
            }
        }
        
        return new PolicyEvaluationResult(false, "No policies matched - default deny", results);
    }
    
    /**
     * 정책 평가 결과를 담는 Record
     */
    public record PolicyEvaluationResult(
        boolean allowed,
        String reason,
        List<PolicyResult> policyResults
    ) {}
    
    /**
     * 개별 정책 평가 결과를 담는 Record
     */
    public record PolicyResult(
        String policyName,
        EffectType effect,
        EvaluationResult result
    ) {}
}