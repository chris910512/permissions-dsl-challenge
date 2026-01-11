프로그램 구현 및 테스트 코드 작성은 모두 Claude Code를 활용해서 진행했습니다.
- Claude Pro Plan, Sonnet 4.5

근거:
1. 구현하고자 하는 시스템이 상대적으로 작고, 새로 시작하는 신규 프로젝트 입니다. (개인적으로는 0->1을 만드는데는 AI Agents가 매우 효과적이라고 생각합니다.)
2. 구현하고자 하는 시스템에 대한 레퍼런스가 이미 온라인에 많이 공개되어 있습니다. AI Agents가 이미 학습했을 확률이 높고, 빠르게 적절한 코드를 생성할 수 있을 것으로 판단했습니다.
3. 최근 Vibe Coding, Claude Code 사용 팁 등을 많이 참고하면서 학습하고 있는데 실전에서도 효과적일지 개인적으로도 검증해보고 싶었습니다. 

아래 # 숫자 순서대로 Claude Code에게 작업을 지시했습니다.

#1

권한 DSL (Domain Specific Language) 시스템 설계 및 구현을 시작할거야.
Java 21를 기준으로 작업해줘.

@README.md 파일에 typescript로 정의된 User, Team, Project, Document, ProjectMembership, TeamMembership 엔티티를 Java 클래스로 변환해줘.
Java 21의 Record, Sealed Classes, Enum 등을 활용해서 불변성과 타입 안전성을 확보해줘.

#2

@DESIDN.md 파일에 정의된 표현식 평가 흐름을 참고해서 Expression DSL을 설계해줘.

핵심 설계 원칙:
- Expression DSL 구조 정의       
- ExpressionEvaluator 클래스 구현
- 3-value 논리 및 단락 평가 구현

#3

PolicyEngine 클래스 및 연관된 프로그램을 구현해줘
다음 제약사항을 만족하도록 작성해줘

**권한 종류**:
- `can_view`: 문서 보기
- `can_edit`: 문서 편집
- `can_delete`: 문서 삭제
- `can_share`: 문서 공유 설정 변경

**정책 예시**:
1. 삭제된 문서는 아무도 편집/삭제할 수 없음 (Deny)
2. 문서 생성자는 모든 권한을 가짐 (Allow)
3. 프로젝트의 editor/admin 역할을 가진 사용자는 편집 가능 (Allow)
4. 팀의 admin 역할을 가진 사용자는 팀 내 모든 프로젝트의 문서에 대해 can_view, can_edit, can_share 권한을 가짐 (Allow)
5. private 프로젝트의 문서는 프로젝트 멤버 또는 팀 admin만 접근 가능 (Deny for others)
6. free 플랜 팀의 문서는 공유 설정 변경 불가 (Deny)
7. publicLinkEnabled가 true인 문서는 누구나 볼 수 있음 (Allow)

#4

DataLoader 인터페이스를 설계해줘
데이터베이스 연결 없이 메모리 기반으로 동작할 수 있게끔 작성해줘
Java 21의 인터페이스와 제네릭을 활용해서 타입 안전성을 확보해줘.

#5 - 시나리오1~6 까지 아래 내용을 반복해서 지시

아래 테스트 케이스를 Java로 구현해줘
정확히 이 테스트 데이터를 활용해 예상 결과를 확인할 수 있게끔 테스트를 1개만 작성해줘

### 시나리오 1: 일반 프로젝트 멤버
```typescript
const user = { id: "u1", email: "user@example.com" };
const team = { id: "t1", plan: "pro" };
const project = { id: "p1", teamId: "t1", visibility: "private" };
const document = {
  id: "d1",
  projectId: "p1",
  creatorId: "u2",
  deletedAt: null,
  publicLinkEnabled: false
};
const teamMembership = { userId: "u1", teamId: "t1", role: "viewer" };
const projectMembership = { userId: "u1", projectId: "p1", role: "editor" };

// 예상 결과:
// can_view: true (프로젝트 멤버이고 private)
// can_edit: true (editor 역할)
// can_delete: false (생성자가 아님)
// can_share: true (pro 플랜)
```

