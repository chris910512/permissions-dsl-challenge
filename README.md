# 권한 DSL 시스템

협업 기반 문서 관리 플랫폼을 위한 권한 Domain Specific Language (DSL) 시스템입니다.
설계문서: [DESIGN.md](DESIGN.md)
AI 코드 생성 기록: [AI_AGENT.md](AI_AGENT.md)

## 설치 및 실행 방법

### 요구사항

- Java 21 이상
- Gradle 8.0 이상 (또는 Gradle Wrapper 사용)

### 1. 프로젝트 클론

```bash
git clone [repository-url]
```

### 2. 빌드

```bash
./gradlew build
```

### 3. 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 시나리오 테스트
./gradlew test --tests Scenario1ExactTest
./gradlew test --tests Scenario2ExactTest
./gradlew test --tests Scenario3ExactTest
./gradlew test --tests Scenario4ExactTest
./gradlew test --tests Scenario5ExactTest
./gradlew test --tests Scenario6ExactTest
```

## 7가지 표준 정책

1. 삭제된 문서 거부 - 삭제된 문서는 편집/삭제/공유 불가
2. 문서 생성자 권한 - 생성자는 모든 권한 보유
3. 프로젝트 편집자 권한 - Editor/Admin 역할은 편집 가능
4. 팀 관리자 권한 - 팀 Admin은 팀 내 모든 문서 접근
5. Private 프로젝트 제한 - 멤버만 Private 프로젝트 접근
6. Free 플랜 공유 제한 - Free 플랜은 공유 기능 제한
7. Public Link 허용 - 공개 링크 활성화 시 누구나 열람

## 개선 가능한 부분 및 제약사항

### 현재 제약사항

1. Expression 제한: 현재는 AND, OR, NOT, BINARY만 지원, 통계 함수(COUNT, SUM, AVG 등) 또는 서브쿼리 등 지원 고려 필요
2. DataLoader 단순화: Java 메모리 기반 구현, 실제 DB 연동 및 복잡한 쿼리 미구현

### 향후 개선 방향

1. Expression 고도화: 집계, 서브쿼리, IN/EXISTS 연산자 추가
2. DataLoader 고도화: JPA/Hibernate 연동으로 실제 DB 지원, 복잡한 쿼리 처리, 캐시 및 지연로딩 등 고려

