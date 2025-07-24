package icns.crawling.repository;

import icns.crawling.model.CaseDecisionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 사건 판결문(CaseDecisionDTO) 엔티티에 대한 데이터베이스 접근 인터페이스
public interface CaseDecisionRepo extends JpaRepository<CaseDecisionDTO, Integer> {
    /**
     * 특정 사건(caseId)에 해당하는 모든 판결문 정보를 조회하는 메서드
     *
     * @param id 사건 ID (case_id)
     * @return 해당 사건 ID와 연결된 CaseDecisionDTO 리스트
     */
    List<CaseDecisionDTO> findAllByCaseId(int id);
    
    /**
     * 전체 판결문 레코드 수를 반환
     *
     * @return 총 판결문 개수
     */
    long count();
}
