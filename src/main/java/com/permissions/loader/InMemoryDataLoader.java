package com.permissions.loader;

import com.permissions.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 메모리 기반 데이터 로더 구현체
 * 테스트 및 개발 목적으로 사용되는 DataLoader
 */
public class InMemoryDataLoader implements DataLoader {
    
    // 엔티티별 데이터 저장소
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<String, Project> projects = new HashMap<>();
    private final Map<String, Document> documents = new HashMap<>();
    private final List<TeamMembership> teamMemberships = new ArrayList<>();
    private final List<ProjectMembership> projectMemberships = new ArrayList<>();
    
    /**
     * 사용자 추가
     */
    public InMemoryDataLoader addUser(User user) {
        users.put(user.id(), user);
        return this;
    }
    
    /**
     * 팀 추가
     */
    public InMemoryDataLoader addTeam(Team team) {
        teams.put(team.id(), team);
        return this;
    }
    
    /**
     * 프로젝트 추가
     */
    public InMemoryDataLoader addProject(Project project) {
        projects.put(project.id(), project);
        return this;
    }
    
    /**
     * 문서 추가
     */
    public InMemoryDataLoader addDocument(Document document) {
        documents.put(document.id(), document);
        return this;
    }
    
    /**
     * 팀 멤버십 추가
     */
    public InMemoryDataLoader addTeamMembership(TeamMembership teamMembership) {
        teamMemberships.add(teamMembership);
        return this;
    }
    
    /**
     * 프로젝트 멤버십 추가
     */
    public InMemoryDataLoader addProjectMembership(ProjectMembership projectMembership) {
        projectMemberships.add(projectMembership);
        return this;
    }
    
    /**
     * 모든 데이터 초기화
     */
    public InMemoryDataLoader clear() {
        users.clear();
        teams.clear();
        projects.clear();
        documents.clear();
        teamMemberships.clear();
        projectMemberships.clear();
        return this;
    }

    @Override
    public Map<String, Map<String, Object>> loadData(Object resource, Object user, Set<String> requiredTables) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for (String tableName : requiredTables) {
            switch (tableName) {
                case "user" -> loadUserData(user, result);
                case "team" -> loadTeamData(resource, user, result);
                case "project" -> loadProjectData(resource, result);
                case "document" -> loadDocumentData(resource, result);
                case "teamMembership" -> loadTeamMembershipData(user, result);
                case "projectMembership" -> loadProjectMembershipData(user, result);
                default -> throw new IllegalArgumentException("Unknown table: " + tableName);
            }
        }
        
        return result;
    }
    
    /**
     * 사용자 데이터 로드
     */
    private void loadUserData(Object user, Map<String, Map<String, Object>> result) {
        if (user instanceof User userEntity) {
            result.put("user", entityToMap(userEntity));
        } else if (user instanceof String userId) {
            User foundUser = users.get(userId);
            if (foundUser != null) {
                result.put("user", entityToMap(foundUser));
            }
        }
    }
    
    /**
     * 팀 데이터 로드 (문서 → 프로젝트 → 팀 경로)
     */
    private void loadTeamData(Object resource, Object user, Map<String, Map<String, Object>> result) {
        String teamId = null;
        
        if (resource instanceof Document document) {
            Project project = projects.get(document.projectId());
            if (project != null) {
                teamId = project.teamId();
            }
        } else if (resource instanceof Project project) {
            teamId = project.teamId();
        } else if (resource instanceof Team team) {
            teamId = team.id();
        }
        
        // 사용자의 팀 멤버십을 통해서도 팀 찾기
        if (teamId == null && user instanceof User userEntity) {
            teamId = teamMemberships.stream()
                .filter(tm -> tm.userId().equals(userEntity.id()))
                .findFirst()
                .map(TeamMembership::teamId)
                .orElse(null);
        }
        
        if (teamId != null) {
            Team team = teams.get(teamId);
            if (team != null) {
                result.put("team", entityToMap(team));
            }
        }
    }
    
    /**
     * 프로젝트 데이터 로드
     */
    private void loadProjectData(Object resource, Map<String, Map<String, Object>> result) {
        String projectId = null;
        
        if (resource instanceof Document document) {
            projectId = document.projectId();
        } else if (resource instanceof Project project) {
            projectId = project.id();
        }
        
        if (projectId != null) {
            Project project = projects.get(projectId);
            if (project != null) {
                result.put("project", entityToMap(project));
            }
        }
    }
    
    /**
     * 문서 데이터 로드
     */
    private void loadDocumentData(Object resource, Map<String, Map<String, Object>> result) {
        if (resource instanceof Document document) {
            result.put("document", entityToMap(document));
        } else if (resource instanceof String documentId) {
            Document document = documents.get(documentId);
            if (document != null) {
                result.put("document", entityToMap(document));
            }
        }
    }
    
    /**
     * 팀 멤버십 데이터 로드
     */
    private void loadTeamMembershipData(Object user, Map<String, Map<String, Object>> result) {
        String userId = null;
        
        if (user instanceof User userEntity) {
            userId = userEntity.id();
        } else if (user instanceof String userIdStr) {
            userId = userIdStr;
        }
        
        if (userId != null) {
            String finalUserId = userId;
            TeamMembership membership = teamMemberships.stream()
                .filter(tm -> tm.userId().equals(finalUserId))
                .findFirst()
                .orElse(null);
                
            if (membership != null) {
                result.put("teamMembership", entityToMap(membership));
            }
        }
    }
    
    /**
     * 프로젝트 멤버십 데이터 로드
     */
    private void loadProjectMembershipData(Object user, Map<String, Map<String, Object>> result) {
        String userId = null;
        
        if (user instanceof User userEntity) {
            userId = userEntity.id();
        } else if (user instanceof String userIdStr) {
            userId = userIdStr;
        }
        
        if (userId != null) {
            String finalUserId = userId;
            ProjectMembership membership = projectMemberships.stream()
                .filter(pm -> pm.userId().equals(finalUserId))
                .findFirst()
                .orElse(null);
                
            if (membership != null) {
                result.put("projectMembership", entityToMap(membership));
            }
        }
    }
    
    /**
     * 엔티티를 Map으로 변환하는 헬퍼 메서드
     */
    private Map<String, Object> entityToMap(Object entity) {
        Map<String, Object> map = new HashMap<>();
        
        switch (entity) {
            case User user -> {
                map.put("id", user.id());
                map.put("email", user.email());
                map.put("name", user.name());
            }
            case Team team -> {
                map.put("id", team.id());
                map.put("name", team.name());
                map.put("plan", team.plan().toString());
            }
            case Project project -> {
                map.put("id", project.id());
                map.put("name", project.name());
                map.put("teamId", project.teamId());
                map.put("visibility", project.visibility().toString());
            }
            case Document document -> {
                map.put("id", document.id());
                map.put("title", document.title());
                map.put("projectId", document.projectId());
                map.put("creatorId", document.creatorId());
                map.put("deletedAt", document.deletedAt());
                map.put("publicLinkEnabled", document.publicLinkEnabled());
            }
            case TeamMembership teamMembership -> {
                map.put("userId", teamMembership.userId());
                map.put("teamId", teamMembership.teamId());
                map.put("role", teamMembership.role().toString());
            }
            case ProjectMembership projectMembership -> {
                map.put("userId", projectMembership.userId());
                map.put("projectId", projectMembership.projectId());
                map.put("role", projectMembership.role().toString());
            }
            default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
        }
        
        return map;
    }
    
    /**
     * 디버깅을 위한 저장된 데이터 조회
     */
    public void printStoredData() {
        System.out.println("=== Stored Data ===");
        System.out.println("Users: " + users.values());
        System.out.println("Teams: " + teams.values());
        System.out.println("Projects: " + projects.values());
        System.out.println("Documents: " + documents.values());
        System.out.println("TeamMemberships: " + teamMemberships);
        System.out.println("ProjectMemberships: " + projectMemberships);
    }
    
    /**
     * 특정 사용자의 모든 멤버십 조회
     */
    public List<TeamMembership> getTeamMembershipsForUser(String userId) {
        return teamMemberships.stream()
            .filter(tm -> tm.userId().equals(userId))
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 사용자의 모든 프로젝트 멤버십 조회
     */
    public List<ProjectMembership> getProjectMembershipsForUser(String userId) {
        return projectMemberships.stream()
            .filter(pm -> pm.userId().equals(userId))
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 팀의 모든 프로젝트 조회
     */
    public List<Project> getProjectsForTeam(String teamId) {
        return projects.values().stream()
            .filter(p -> p.teamId().equals(teamId))
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 프로젝트의 모든 문서 조회
     */
    public List<Document> getDocumentsForProject(String projectId) {
        return documents.values().stream()
            .filter(d -> d.projectId().equals(projectId))
            .collect(Collectors.toList());
    }
}