package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 3: Free 플랜 제한 테스트
 * Free 플랜에서는 공유 기능이 제한되는지 검증
 */
class Scenario3ExactTest {

    @Test
    void testScenario3FreePlanLimitation() {
        // Given - README.md의 정확한 시나리오 3 데이터
        var user = new User("u1", "user@example.com", "User");
        var documentCreator = new User("u2", "creator@example.com", "Creator");
        var team = new Team("t1", "Team", PlanType.FREE); // Free 플랜
        var project = new Project("p1", "Project", "t1", VisibilityType.PUBLIC);
        var document = new Document("d1", "Document", "p1", "u2", null, false); // u2가 생성자
        var teamMembership = new TeamMembership("u1", "t1", RoleType.VIEWER);
        var projectMembership = new ProjectMembership("u1", "p1", RoleType.ADMIN);
        
        // 데이터 로더 설정
        var dataLoader = new InMemoryDataLoader();
        dataLoader
            .addUser(user)
            .addUser(documentCreator)
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
            "can_view: true (프로젝트 멤버)");
        
        assertTrue(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: true (프로젝트 admin)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (생성자가 아님)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: false (free 플랜 - DENY 정책)");
        
        // 결과 출력
        System.out.println("=== 시나리오 3 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (프로젝트 멤버)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (프로젝트 admin)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (생성자가 아님)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (free 플랜 - DENY 정책)");
    }
}