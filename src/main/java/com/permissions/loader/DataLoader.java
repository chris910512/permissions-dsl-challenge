package com.permissions.loader;

import java.util.Map;
import java.util.Set;

/**
 * 데이터 로딩을 추상화하는 인터페이스
 * 정책 평가에 필요한 엔티티 데이터를 로드
 */
public interface DataLoader {
    
    /**
     * 정책 평가에 필요한 데이터를 로드
     * 
     * @param resource 대상 리소스 (예: Document)
     * @param user 사용자 (예: User)
     * @param requiredTables 필요한 테이블 목록 (예: ["user", "document", "project"])
     * @return 테이블명을 키로 하는 엔티티 데이터 맵
     *         예: {"user" -> {"id": "u1", "name": "User1"}, "document" -> {"id": "d1", "title": "Doc1"}}
     */
    Map<String, Map<String, Object>> loadData(Object resource, Object user, Set<String> requiredTables);
}