package com.permissions.policy;

import com.permissions.model.PermissionType;
import com.permissions.dsl.*;
import java.util.Set;
import java.util.List;

import static com.permissions.model.PermissionType.*;
import static com.permissions.policy.EffectType.*;
import static com.permissions.dsl.OperatorType.*;

/**
 * 7가지 표준 정책을 정의하는 팩토리 클래스
 * 협업 문서 관리 플랫폼의 권한 모델을 구현
 */
public class StandardPolicies {

    /**
     * 1. 삭제된 문서는 아무도 편집/삭제할 수 없음 (Deny)
     */
    public static Policy deletedDocumentPolicy() {
        return new Policy(
            "deleted_document_deny",
            "삭제된 문서는 편집/삭제/공유 불가",
            DENY,
            Set.of(CAN_EDIT, CAN_DELETE, CAN_SHARE),
            new BinaryExpressionDef("document.deletedAt", NOT_EQUALS, null),
            Set.of("document")
        );
    }

    /**
     * 2. 문서 생성자는 모든 권한을 가짐 (Allow)
     */
    public static Policy documentCreatorPolicy() {
        return new Policy(
            "document_creator_allow",
            "문서 생성자는 모든 권한 보유",
            ALLOW,
            Set.of(CAN_VIEW, CAN_EDIT, CAN_DELETE, CAN_SHARE),
            new BinaryExpressionDef("user.id", EQUALS, new FieldReference("document.creatorId")),
            Set.of("user", "document")
        );
    }

    /**
     * 3. 프로젝트의 editor/admin 역할을 가진 사용자는 편집 가능 (Allow)
     */
    public static Policy projectEditorPolicy() {
        return new Policy(
            "project_editor_allow",
            "프로젝트 editor/admin은 편집 가능",
            ALLOW,
            Set.of(CAN_VIEW, CAN_EDIT),
            new AndExpressionDef(List.of(
                new BinaryExpressionDef("projectMembership.userId", EQUALS, new FieldReference("user.id")),
                new BinaryExpressionDef("projectMembership.projectId", EQUALS, new FieldReference("document.projectId")),
                new OrExpressionDef(List.of(
                    new BinaryExpressionDef("projectMembership.role", EQUALS, "EDITOR"),
                    new BinaryExpressionDef("projectMembership.role", EQUALS, "ADMIN")
                ))
            )),
            Set.of("user", "document", "projectMembership")
        );
    }

    /**
     * 4. 팀의 admin 역할을 가진 사용자는 팀 내 모든 프로젝트의 문서에 대해 권한을 가짐 (Allow)
     */
    public static Policy teamAdminPolicy() {
        return new Policy(
            "team_admin_allow",
            "팀 admin은 팀 내 모든 문서에 대해 권한 보유",
            ALLOW,
            Set.of(CAN_VIEW, CAN_EDIT, CAN_SHARE),
            new AndExpressionDef(List.of(
                new BinaryExpressionDef("teamMembership.userId", EQUALS, new FieldReference("user.id")),
                new BinaryExpressionDef("teamMembership.teamId", EQUALS, new FieldReference("project.teamId")),
                new BinaryExpressionDef("project.id", EQUALS, new FieldReference("document.projectId")),
                new BinaryExpressionDef("teamMembership.role", EQUALS, "ADMIN")
            )),
            Set.of("user", "document", "project", "teamMembership")
        );
    }

    /**
     * 5. private 프로젝트의 문서는 프로젝트 멤버 또는 팀 admin만 접근 가능 (Deny for others)
     */
    public static Policy privateProjectDenyPolicy() {
        return new Policy(
            "private_project_deny",
            "private 프로젝트는 멤버만 접근 가능",
            DENY,
            Set.of(CAN_VIEW, CAN_EDIT, CAN_DELETE, CAN_SHARE),
            new AndExpressionDef(List.of(
                new BinaryExpressionDef("project.visibility", EQUALS, "PRIVATE"),
                new BinaryExpressionDef("project.id", EQUALS, new FieldReference("document.projectId")),
                // 프로젝트 멤버가 아니고 팀 admin도 아님
                new NotExpressionDef(new OrExpressionDef(List.of(
                    // 프로젝트 멤버십 체크
                    new AndExpressionDef(List.of(
                        new BinaryExpressionDef("projectMembership.userId", EQUALS, new FieldReference("user.id")),
                        new BinaryExpressionDef("projectMembership.projectId", EQUALS, new FieldReference("document.projectId"))
                    )),
                    // 팀 admin 체크
                    new AndExpressionDef(List.of(
                        new BinaryExpressionDef("teamMembership.userId", EQUALS, new FieldReference("user.id")),
                        new BinaryExpressionDef("teamMembership.teamId", EQUALS, new FieldReference("project.teamId")),
                        new BinaryExpressionDef("teamMembership.role", EQUALS, "ADMIN")
                    ))
                )))
            )),
            Set.of("user", "document", "project", "teamMembership", "projectMembership")
        );
    }

    /**
     * 6. free 플랜 팀의 문서는 공유 설정 변경 불가 (Deny)
     */
    public static Policy freePlanShareDenyPolicy() {
        return new Policy(
            "free_plan_share_deny",
            "free 플랜은 공유 설정 변경 불가",
            DENY,
            Set.of(CAN_SHARE),
            new AndExpressionDef(List.of(
                new BinaryExpressionDef("project.id", EQUALS, new FieldReference("document.projectId")),
                new BinaryExpressionDef("team.id", EQUALS, new FieldReference("project.teamId")),
                new BinaryExpressionDef("team.plan", EQUALS, "FREE")
            )),
            Set.of("document", "project", "team")
        );
    }

    /**
     * 7. publicLinkEnabled가 true인 문서는 누구나 볼 수 있음 (Allow)
     */
    public static Policy publicLinkPolicy() {
        return new Policy(
            "public_link_allow",
            "공개 링크 문서는 누구나 볼 수 있음",
            ALLOW,
            Set.of(CAN_VIEW),
            new BinaryExpressionDef("document.publicLinkEnabled", EQUALS, true),
            Set.of("document")
        );
    }

    /**
     * 모든 표준 정책을 반환
     */
    public static List<Policy> getAllStandardPolicies() {
        return List.of(
            deletedDocumentPolicy(),
            documentCreatorPolicy(),
            projectEditorPolicy(),
            teamAdminPolicy(),
            privateProjectDenyPolicy(),
            freePlanShareDenyPolicy(),
            publicLinkPolicy()
        );
    }
}