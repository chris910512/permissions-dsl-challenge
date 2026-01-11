package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 1 정확한 테스트 데이터를 활용한 단일 테스트
 * AI_AGENT.md의 TypeScript 데이터와 동일한 구조로 검증
 */
class Scenario1ExactTest {

    @Test
    void testScenario1ExactData() {
        // Given - AI_AGENT.md의 정확한 데이터 구조
        var user = new User("u1", "user@example.com", "User");
        var team = new Team("t1", "Team", PlanType.PRO);
        var project = new Project("p1", "Project", "t1", VisibilityType.PRIVATE);
        var document = new Document("d1", "Document", "p1", "u2", null, false);
        var teamMembership = new TeamMembership("u1", "t1", RoleType.VIEWER);
        var projectMembership = new ProjectMembership("u1", "p1", RoleType.EDITOR);
        
        // 데이터 로더 설정
        var dataLoader = new InMemoryDataLoader();
        dataLoader
            .addUser(user)
            .addUser(new User("u2", "creator@example.com", "Creator")) // 문서 생성자
            .addTeam(team)
            .addProject(project)
            .addDocument(document)
            .addTeamMembership(teamMembership)
            .addProjectMembership(projectMembership);
        
        // 정책 엔진 설정
        var evaluator = new ExpressionEvaluator();
        var policyEngine = new PolicyEngine(dataLoader, evaluator);
        policyEngine.addPolicies(StandardPolicies.getAllStandardPolicies());
        
        // When & Then - 예상 결과 검증
        assertTrue(policyEngine.hasPermission(document, user, CAN_VIEW), 
            "can_view: true (프로젝트 멤버이고 private)");
        
        assertTrue(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: true (editor 역할)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (생성자가 아님)");
        
        assertTrue(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: true (pro 플랜)");
        
        // 결과 출력
        System.out.println("=== 시나리오 1 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (프로젝트 멤버이고 private)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (editor 역할)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (생성자가 아님)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (pro 플랜)");
    }
}