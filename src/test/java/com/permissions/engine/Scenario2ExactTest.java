package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 2: 삭제된 문서 테스트
 * 문서가 삭제된 경우 DENY 정책이 우선 적용되는지 검증
 */
class Scenario2ExactTest {

    @Test
    void testScenario2DeletedDocument() {
        // Given - README.md의 정확한 시나리오 2 데이터
        var user = new User("u1", "creator@example.com", "Creator");
        var team = new Team("t1", "Team", PlanType.PRO);
        var project = new Project("p1", "Project", "t1", VisibilityType.PRIVATE);
        var document = new Document("d1", "Document", "p1", "u1", Instant.now(), false); // deletedAt = 현재시간
        var teamMembership = new TeamMembership("u1", "t1", RoleType.ADMIN);
        var projectMembership = new ProjectMembership("u1", "p1", RoleType.ADMIN);
        
        // 데이터 로더 설정
        var dataLoader = new InMemoryDataLoader();
        dataLoader
            .addUser(user)
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
            "can_view: true (생성자)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: false (삭제됨 - DENY 정책)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (삭제됨 - DENY 정책)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: false (삭제됨 - DENY 정책)");
        
        // 결과 출력
        System.out.println("=== 시나리오 2 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (생성자)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (삭제됨 - DENY 정책)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (삭제됨 - DENY 정책)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (삭제됨 - DENY 정책)");
    }
}