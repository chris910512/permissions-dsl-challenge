package com.permissions.loader;

import com.permissions.model.*;
import java.time.Instant;

/**
 * 테스트 데이터를 쉽게 생성하고 설정하는 빌더 클래스
 * 다양한 시나리오 테스트를 위한 샘플 데이터 제공
 */
public class TestDataBuilder {
    
    private final InMemoryDataLoader dataLoader;
    
    public TestDataBuilder(InMemoryDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    /**
     * 기본 테스트 시나리오 데이터 설정
     * 사용자, 팀, 프로젝트, 문서, 멤버십을 포함하는 완전한 데이터셋
     */
    public TestDataBuilder setupBasicScenario() {
        // 사용자들
        var alice = new User("u1", "alice@example.com", "Alice");
        var bob = new User("u2", "bob@example.com", "Bob"); 
        var charlie = new User("u3", "charlie@example.com", "Charlie");
        var diana = new User("u4", "diana@example.com", "Diana");
        
        // 팀들
        var teamA = new Team("t1", "Team Alpha", PlanType.PRO);
        var teamB = new Team("t2", "Team Beta", PlanType.FREE);
        var teamC = new Team("t3", "Team Gamma", PlanType.ENTERPRISE);
        
        // 프로젝트들
        var projectX = new Project("p1", "Project X", "t1", VisibilityType.PRIVATE);
        var projectY = new Project("p2", "Project Y", "t1", VisibilityType.PUBLIC);
        var projectZ = new Project("p3", "Project Z", "t2", VisibilityType.PRIVATE);
        
        // 문서들
        var doc1 = new Document("d1", "Important Document", "p1", "u1", null, false);
        var doc2 = new Document("d2", "Public Document", "p2", "u2", null, true);
        var doc3 = new Document("d3", "Deleted Document", "p1", "u3", Instant.now(), false);
        var doc4 = new Document("d4", "Free Plan Document", "p3", "u4", null, false);
        
        // 팀 멤버십
        var aliceTeamAdmin = new TeamMembership("u1", "t1", RoleType.ADMIN);
        var bobTeamEditor = new TeamMembership("u2", "t1", RoleType.EDITOR);
        var charlieTeamViewer = new TeamMembership("u3", "t1", RoleType.VIEWER);
        var dianaTeamAdmin = new TeamMembership("u4", "t2", RoleType.ADMIN);
        
        // 프로젝트 멤버십
        var aliceProjectAdmin = new ProjectMembership("u1", "p1", RoleType.ADMIN);
        var bobProjectEditor = new ProjectMembership("u2", "p2", RoleType.EDITOR);
        var charlieProjectViewer = new ProjectMembership("u3", "p1", RoleType.VIEWER);
        var dianaProjectEditor = new ProjectMembership("u4", "p3", RoleType.EDITOR);
        
        // 데이터 로더에 추가
        dataLoader
            .addUser(alice).addUser(bob).addUser(charlie).addUser(diana)
            .addTeam(teamA).addTeam(teamB).addTeam(teamC)
            .addProject(projectX).addProject(projectY).addProject(projectZ)
            .addDocument(doc1).addDocument(doc2).addDocument(doc3).addDocument(doc4)
            .addTeamMembership(aliceTeamAdmin).addTeamMembership(bobTeamEditor)
            .addTeamMembership(charlieTeamViewer).addTeamMembership(dianaTeamAdmin)
            .addProjectMembership(aliceProjectAdmin).addProjectMembership(bobProjectEditor)
            .addProjectMembership(charlieProjectViewer).addProjectMembership(dianaProjectEditor);
        
        return this;
    }
    
    /**
     * 문서 생성자 시나리오 설정
     */
    public TestDataBuilder setupDocumentCreatorScenario() {
        var creator = new User("creator1", "creator@example.com", "Creator");
        var team = new Team("team1", "Creator Team", PlanType.PRO);
        var project = new Project("project1", "Creator Project", "team1", VisibilityType.PRIVATE);
        var document = new Document("doc1", "Creator's Document", "project1", "creator1", null, false);
        
        dataLoader
            .addUser(creator)
            .addTeam(team)
            .addProject(project)
            .addDocument(document);
        
        return this;
    }
    
    /**
     * Private 프로젝트 접근 시나리오 설정
     */
    public TestDataBuilder setupPrivateProjectScenario() {
        var member = new User("member1", "member@example.com", "Member");
        var outsider = new User("outsider1", "outsider@example.com", "Outsider");
        var teamAdmin = new User("admin1", "admin@example.com", "Admin");
        
        var team = new Team("team1", "Private Team", PlanType.PRO);
        var privateProject = new Project("private1", "Private Project", "team1", VisibilityType.PRIVATE);
        var document = new Document("doc1", "Private Document", "private1", "member1", null, false);
        
        var memberTeamMembership = new TeamMembership("member1", "team1", RoleType.VIEWER);
        var adminTeamMembership = new TeamMembership("admin1", "team1", RoleType.ADMIN);
        var memberProjectMembership = new ProjectMembership("member1", "private1", RoleType.VIEWER);
        
        dataLoader
            .addUser(member).addUser(outsider).addUser(teamAdmin)
            .addTeam(team)
            .addProject(privateProject)
            .addDocument(document)
            .addTeamMembership(memberTeamMembership).addTeamMembership(adminTeamMembership)
            .addProjectMembership(memberProjectMembership);
        
        return this;
    }
    
    /**
     * Free 플랜 제한 시나리오 설정
     */
    public TestDataBuilder setupFreePlanScenario() {
        var user = new User("user1", "user@example.com", "User");
        var freeTeam = new Team("free1", "Free Team", PlanType.FREE);
        var proTeam = new Team("pro1", "Pro Team", PlanType.PRO);
        var freeProject = new Project("freeProj1", "Free Project", "free1", VisibilityType.PUBLIC);
        var proProject = new Project("proProj1", "Pro Project", "pro1", VisibilityType.PUBLIC);
        var freeDoc = new Document("freeDoc1", "Free Document", "freeProj1", "user1", null, false);
        var proDoc = new Document("proDoc1", "Pro Document", "proProj1", "user1", null, false);
        
        var freeTeamMembership = new TeamMembership("user1", "free1", RoleType.ADMIN);
        var proTeamMembership = new TeamMembership("user1", "pro1", RoleType.ADMIN);
        var freeProjectMembership = new ProjectMembership("user1", "freeProj1", RoleType.ADMIN);
        var proProjectMembership = new ProjectMembership("user1", "proProj1", RoleType.ADMIN);
        
        dataLoader
            .addUser(user)
            .addTeam(freeTeam).addTeam(proTeam)
            .addProject(freeProject).addProject(proProject)
            .addDocument(freeDoc).addDocument(proDoc)
            .addTeamMembership(freeTeamMembership).addTeamMembership(proTeamMembership)
            .addProjectMembership(freeProjectMembership).addProjectMembership(proProjectMembership);
        
        return this;
    }
    
    /**
     * Public Link 시나리오 설정
     */
    public TestDataBuilder setupPublicLinkScenario() {
        var owner = new User("owner1", "owner@example.com", "Owner");
        var stranger = new User("stranger1", "stranger@example.com", "Stranger");
        
        var team = new Team("team1", "Link Team", PlanType.PRO);
        var project = new Project("project1", "Link Project", "team1", VisibilityType.PRIVATE);
        var publicDoc = new Document("public1", "Public Link Document", "project1", "owner1", null, true);
        var privateDoc = new Document("private1", "Private Document", "project1", "owner1", null, false);
        
        var ownerTeamMembership = new TeamMembership("owner1", "team1", RoleType.ADMIN);
        var ownerProjectMembership = new ProjectMembership("owner1", "project1", RoleType.ADMIN);
        
        dataLoader
            .addUser(owner).addUser(stranger)
            .addTeam(team)
            .addProject(project)
            .addDocument(publicDoc).addDocument(privateDoc)
            .addTeamMembership(ownerTeamMembership)
            .addProjectMembership(ownerProjectMembership);
        
        return this;
    }
    
    /**
     * 삭제된 문서 시나리오 설정
     */
    public TestDataBuilder setupDeletedDocumentScenario() {
        var creator = new User("creator1", "creator@example.com", "Creator");
        var admin = new User("admin1", "admin@example.com", "Admin");
        
        var team = new Team("team1", "Delete Team", PlanType.PRO);
        var project = new Project("project1", "Delete Project", "team1", VisibilityType.PUBLIC);
        var deletedDoc = new Document("deleted1", "Deleted Document", "project1", "creator1", Instant.now(), false);
        var activeDoc = new Document("active1", "Active Document", "project1", "creator1", null, false);
        
        var creatorTeamMembership = new TeamMembership("creator1", "team1", RoleType.EDITOR);
        var adminTeamMembership = new TeamMembership("admin1", "team1", RoleType.ADMIN);
        var creatorProjectMembership = new ProjectMembership("creator1", "project1", RoleType.EDITOR);
        var adminProjectMembership = new ProjectMembership("admin1", "project1", RoleType.ADMIN);
        
        dataLoader
            .addUser(creator).addUser(admin)
            .addTeam(team)
            .addProject(project)
            .addDocument(deletedDoc).addDocument(activeDoc)
            .addTeamMembership(creatorTeamMembership).addTeamMembership(adminTeamMembership)
            .addProjectMembership(creatorProjectMembership).addProjectMembership(adminProjectMembership);
        
        return this;
    }
    
    /**
     * 팀 Admin 권한 시나리오 설정
     */
    public TestDataBuilder setupTeamAdminScenario() {
        var teamAdmin = new User("teamAdmin1", "teamadmin@example.com", "Team Admin");
        var projectMember = new User("projectMember1", "projectmember@example.com", "Project Member");
        var outsider = new User("outsider1", "outsider@example.com", "Outsider");
        
        var team = new Team("team1", "Admin Team", PlanType.PRO);
        var project = new Project("project1", "Team Project", "team1", VisibilityType.PRIVATE);
        var document = new Document("doc1", "Team Document", "project1", "projectMember1", null, false);
        
        var adminTeamMembership = new TeamMembership("teamAdmin1", "team1", RoleType.ADMIN);
        var memberTeamMembership = new TeamMembership("projectMember1", "team1", RoleType.VIEWER);
        var memberProjectMembership = new ProjectMembership("projectMember1", "project1", RoleType.EDITOR);
        
        dataLoader
            .addUser(teamAdmin).addUser(projectMember).addUser(outsider)
            .addTeam(team)
            .addProject(project)
            .addDocument(document)
            .addTeamMembership(adminTeamMembership).addTeamMembership(memberTeamMembership)
            .addProjectMembership(memberProjectMembership);
        
        return this;
    }
    
    /**
     * 현재 설정된 데이터를 콘솔에 출력
     */
    public TestDataBuilder printData() {
        dataLoader.printStoredData();
        return this;
    }
}