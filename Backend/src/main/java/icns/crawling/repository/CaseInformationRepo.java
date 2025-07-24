package icns.crawling.repository;

import icns.crawling.model.CaseInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

// 사건 기본 정보(CaseInformationDTO)를 관리하는 JPA 리포지토리
public interface CaseInformationRepo extends JpaRepository<CaseInformationDTO, Integer> {

     /**
     * 사건명(case_name)에 특정 키워드가 포함되고, 판결일(decision_date)이 일치하는 사건 정보 조회
     * 
     * @param caseName 사건명 (부분 일치)
     * @param date     판결일 (정확히 일치)
     * @return 조건에 맞는 사건 리스트
     */
    @Query(value = "SELECT * FROM case_info WHERE CASE_NAME LIKE CONCAT('%', :caseName, '%') AND DECISION_DATE = :date;", nativeQuery = true)
    List<CaseInformationDTO> findByCaseNameAndDecisionDate(@Param("caseName") String caseName, @Param("date") Date date);
    
    /**
     * 사건번호(index_no)가 특정 값으로 시작하는 사건을 페이지 단위로 조회
     * ROW_NUMBER() 윈도우 함수를 사용해 페이지네이션 처리
     * 
     * @param indexNo 검색할 사건번호 접두어
     * @param page    페이지 번호 (1부터 시작)
     * @param count   페이지당 데이터 수
     * @return 조건에 맞는 사건 정보 리스트
     */
    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *,\n" +
            "           ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num\n" +
            "    FROM case_info" +
            "    WHERE INDEX_NO LIKE CONCAT(:indexNo, '%') \n" +
            ") AS FilteredCases\n" +
            "WHERE global_row_num BETWEEN :count * (:page - 1) + 1 AND :count * :page;\n", nativeQuery = true)
    List<CaseInformationDTO> searchByIndexNo(@Param("indexNo") String indexNo, @Param("page") int page, @Param("count") int count);


     /**
     * 사건번호(index_no)에 특정 문자열이 포함된 사건의 개수 반환
     * 판결일이 1998년 12월 31일 이후인 데이터만 포함
     *
     * @param indexNo 검색할 사건번호 패턴
     * @return 조건을 만족하는 사건 수
     */
    @Query(value = "SELECT COUNT(*) FROM case_info l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%') AND DECISION_DATE > '1998-12-31'", nativeQuery = true)
    long count(@Param("indexNo") String indexNo);


    /**
     * 전체 사건 정보를 페이지 단위로 조회
     * ROW_NUMBER()를 이용한 오프셋 기반 페이징 처리
     *
     * @param page  페이지 번호 (1부터 시작)
     * @param count 페이지당 데이터 수
     * @return 사건 정보 리스트
     */
    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *,\n" +
            "           ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num\n" +
            "    FROM case_info\n" +
            ") AS FilteredCases\n" +
            "WHERE global_row_num BETWEEN :count * (:page - 1) + 1 AND :count * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchAll(@Param("page") int page, @Param("count") int count);

    /**
     * 전체 사건 수를 반환
     * 
     * @return 사건 전체 개수
     */
    @Query(value = "SELECT COUNT(*) FROM case_info;", nativeQuery = true)
    long countAll();
}
