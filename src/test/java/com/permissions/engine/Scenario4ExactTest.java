package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 4: 팀 Admin - Private 프로젝트 접근 가능 테스트
 * 팀 Admin은 프로젝트 멤버가 아니어도 팀 내 Private 프로젝트에 접근 가능한지 검증
 */
class Scenario4ExactTest {

    @Test
    void testScenario4TeamAdminPrivateAccess() {
        // Given - README.md의 정확한 시나리오 4 데이터
        var user = new User("u1", "admin@example.com", "Admin");
        var documentCreator = new User("u2", "creator@example.com", "Creator");
        var team = new Team("t1", "Team", PlanType.PRO);
        var project = new Project("p1", "Project", "t1", VisibilityType.PRIVATE);
        var document = new Document("d1", "Document", "p1", "u2", null, false); // u2가 생성자
        var teamMembership = new TeamMembership("u1", "t1", RoleType.ADMIN);
        // projectMembership 없음 - 프로젝트에 직접 포함되지 않음
        
        // 데이터 로더 설정
        var dataLoader = new InMemoryDataLoader();
        dataLoader
            .addUser(user)
            .addUser(documentCreator)
            .addTeam(team)
            .addProject(project)
            .addDocument(document)
            .addTeamMembership(teamMembership);
            // projectMembership는 추가하지 않음
        
        // 정책 엔진 설정
        var evaluator = new ExpressionEvaluator();
        var policyEngine = new PolicyEngine(dataLoader, evaluator);
        policyEngine.addPolicies(StandardPolicies.getAllStandardPolicies());
        
        // When & Then - 예상 결과 검증
        assertTrue(policyEngine.hasPermission(document, user, CAN_VIEW), 
            "can_view: true (팀 admin은 팀 내 모든 private 프로젝트 접근 가능)");
        
        assertTrue(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: true (팀 admin 권한으로 편집 가능)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (생성자가 아님)");
        
        assertTrue(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: true (pro 플랜 + 팀 admin)");
        
        // 결과 출력
        System.out.println("=== 시나리오 4 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (팀 admin은 팀 내 모든 private 프로젝트 접근 가능)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (팀 admin 권한으로 편집 가능)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (생성자가 아님)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (pro 플랜 + 팀 admin)");
    }
}