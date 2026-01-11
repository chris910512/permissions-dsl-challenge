package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 6: Public Link 활성화 - 누구나 볼 수 있음 테스트
 * publicLinkEnabled가 true인 문서는 멤버가 아니어도 볼 수 있는지 검증
 */
class Scenario6ExactTest {

    @Test
    void testScenario6PublicLinkAccess() {
        // Given - README.md의 정확한 시나리오 6 데이터
        var user = new User("u1", "guest@example.com", "Guest");
        var documentCreator = new User("u2", "creator@example.com", "Creator");
        var team = new Team("t1", "Team", PlanType.PRO);
        var project = new Project("p1", "Project", "t1", VisibilityType.PRIVATE);
        var document = new Document("d1", "Document", "p1", "u2", null, true); // publicLinkEnabled = true
        // teamMembership 없음 - 팀 멤버가 아님
        // projectMembership 없음 - 프로젝트 멤버가 아님
        
        // 데이터 로더 설정
        var dataLoader = new InMemoryDataLoader();
        dataLoader
            .addUser(user)
            .addUser(documentCreator)
            .addTeam(team)
            .addProject(project)
            .addDocument(document);
            // teamMembership, projectMembership는 추가하지 않음
        
        // 정책 엔진 설정
        var evaluator = new ExpressionEvaluator();
        var policyEngine = new PolicyEngine(dataLoader, evaluator);
        policyEngine.addPolicies(StandardPolicies.getAllStandardPolicies());
        
        // When & Then - 예상 결과 검증
        assertTrue(policyEngine.hasPermission(document, user, CAN_VIEW), 
            "can_view: true (publicLinkEnabled가 true이면 누구나 볼 수 있음)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: false (멤버가 아니므로 편집 불가)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (멤버가 아니므로 삭제 불가)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: false (멤버가 아니므로 공유 설정 변경 불가)");
        
        // 결과 출력
        System.out.println("=== 시나리오 6 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (publicLinkEnabled가 true이면 누구나 볼 수 있음)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (멤버가 아니므로 편집 불가)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (멤버가 아니므로 삭제 불가)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (멤버가 아니므로 공유 설정 변경 불가)");
    }
}