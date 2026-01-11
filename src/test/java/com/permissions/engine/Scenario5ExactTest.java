package com.permissions.engine;

import com.permissions.model.*;
import com.permissions.loader.InMemoryDataLoader;
import com.permissions.evaluator.ExpressionEvaluator;
import com.permissions.policy.StandardPolicies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.permissions.model.PermissionType.*;

/**
 * 시나리오 5: 팀 Editor - Private 프로젝트 접근 불가 테스트
 * 팀 Editor는 프로젝트에 명시적으로 포함되어야만 Private 프로젝트에 접근 가능한지 검증
 */
class Scenario5ExactTest {

    @Test
    void testScenario5TeamEditorPrivateAccessDenied() {
        // Given - README.md의 정확한 시나리오 5 데이터
        var user = new User("u1", "editor@example.com", "Editor");
        var documentCreator = new User("u2", "creator@example.com", "Creator");
        var team = new Team("t1", "Team", PlanType.PRO);
        var project = new Project("p1", "Project", "t1", VisibilityType.PRIVATE);
        var document = new Document("d1", "Document", "p1", "u2", null, false); // u2가 생성자
        var teamMembership = new TeamMembership("u1", "t1", RoleType.EDITOR);
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
        assertFalse(policyEngine.hasPermission(document, user, CAN_VIEW), 
            "can_view: false (팀 editor는 private 프로젝트에 명시적으로 포함되어야 함)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_EDIT), 
            "can_edit: false (접근 불가)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_DELETE), 
            "can_delete: false (접근 불가)");
        
        assertFalse(policyEngine.hasPermission(document, user, CAN_SHARE), 
            "can_share: false (접근 불가)");
        
        // 결과 출력
        System.out.println("=== 시나리오 5 테스트 결과 ===");
        System.out.println("can_view: " + policyEngine.hasPermission(document, user, CAN_VIEW) + " (팀 editor는 private 프로젝트에 명시적으로 포함되어야 함)");
        System.out.println("can_edit: " + policyEngine.hasPermission(document, user, CAN_EDIT) + " (접근 불가)");
        System.out.println("can_delete: " + policyEngine.hasPermission(document, user, CAN_DELETE) + " (접근 불가)");
        System.out.println("can_share: " + policyEngine.hasPermission(document, user, CAN_SHARE) + " (접근 불가)");
    }
}