package com.permissions.model;

import java.time.Instant;

/**
 * 문서 엔티티
 * 
 * @param id 문서 고유 ID
 * @param title 문서 제목
 * @param projectId 소속 프로젝트 ID
 * @param creatorId 문서 생성자 사용자 ID
 * @param deletedAt 삭제 시점 (null이면 삭제되지 않음)
 * @param publicLinkEnabled 공개 링크 활성화 여부
 */
public record Document(
    String id, 
    String title, 
    String projectId, 
    String creatorId,
    Instant deletedAt, 
    boolean publicLinkEnabled
) {}