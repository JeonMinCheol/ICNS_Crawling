package icns.crawling.repository;

import icns.crawling.model.PlaintiffLawyerDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 원고 변호사 정보(PlaintiffLawyerDTO)를 관리하는 JPA 리포지토리 인터페이스
public interface PlaintiffLawyerRepo extends JpaRepository<PlaintiffLawyerDTO, Integer> {
    /**
     * 특정 사건 ID에 포함된 모든 원고 변호사 정보를 조회
     *
     * @param id 사건 ID (case_id)
     * @return 해당 사건에 참여한 원고 변호사 리스트
     */
    List<PlaintiffLawyerDTO> findAllByCaseId(int id);

    /**
     * 특정 변호사 번호에 해당하는 원고 측 사건 기록을 ID 기준으로 정렬하여 조회
     *
     * @param lawyerNo 변호사 고유 번호
     * @return 해당 변호사가 참여한 원고 측 사건 목록 (ID 오름차순 정렬)
     */
    List<PlaintiffLawyerDTO> findAllByLawyerNoOrderById(int lawyerNo);

    /**
     * 특정 변호사 번호에 해당하는 원고 측 사건 기록을 모두 조회
     *
     * @param lawyerNo 변호사 고유 번호
     * @return 해당 변호사의 원고 측 사건 목록
     */
    List<PlaintiffLawyerDTO> findAllByLawyerNo(int lawyerNo);

    /**
     * 특정 변호사 번호와 사건 ID에 해당하는 원고 변호사 정보를 조회
     *
     * @param lawyerNo 변호사 고유 번호
     * @param CaseId   사건 ID
     * @return 해당 사건에서의 해당 변호사 정보 (Optional로 반환)
     */
    Optional<PlaintiffLawyerDTO> findByLawyerNoAndCaseId(int lawyerNo, int CaseId);
}
