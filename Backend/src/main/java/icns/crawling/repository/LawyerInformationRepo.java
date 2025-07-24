package icns.crawling.repository;

import icns.crawling.model.LawyerInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// 변호사 정보(lawyer_info)를 관리하는 JPA 리포지토리
public interface LawyerInformationRepo extends JpaRepository<LawyerInformationDTO, Integer> {
    /**
    * 이름이 정확히 일치하는 변호사를 조회
    *
    * @param lawyer 변호사 이름 (정확 일치)
    * @return 해당 이름을 가진 변호사 정보 (없으면 Optional.empty)
    */
    Optional<LawyerInformationDTO> findByName(String lawyer);

    /**
     * 변호사 이름이 특정 접두어로 시작하는 변호사들을 조회
     * count(처리건수) 내림차순 → win(승소건수) 내림차순 정렬
     * 최대 50명 반환, offset 기반 페이징
     *
     * @param lawyer 검색할 변호사 이름 접두어
     * @param page   OFFSET으로 사용할 값 (페이지 번호 × 50)
     * @return 검색 결과 리스트
     */
    @Query(value ="SELECT * from lawyer_info where LAWYER_NAME like concat(:lawyer, '%') ORDER BY COUNT DESC, WIN DESC LIMIT 50 OFFSET :page;" , nativeQuery = true)
    List<LawyerInformationDTO> searchLawyerByName(@Param("lawyer") String lawyer, @Param("page") int page);

    /**
     * 변호사 이름에 특정 키워드가 포함된 변호사의 총 수를 반환
     *
     * @param lawyer 검색 키워드
     * @return 변호사 수
     */
    @Query(value ="select count(*) from lawyer_info where LAWYER_NAME like concat('%', :lawyer, '%');", nativeQuery = true)
    long count(@Param("lawyer") String lawyer);

    /**
     * 전체 변호사 목록 조회 (이름이 'None'인 항목 제외)
     * 사건(case_info)에 연결된 변호사만 조회
     * count, win 기준 내림차순 정렬, 페이징 지원
     *
     * @param page OFFSET 값 (페이지 번호 × 50)
     * @return 검색 결과 리스트
     */
    @Query(value = "WITH RelevantCases AS (\n" +
            "    SELECT CASE_ID\n" +
            "    FROM case_info \n" +
            "),\n" +
            "LawyerCases AS (\n" +
            "    SELECT LAWYER_NO, CASE_ID\n" +
            "    FROM defendant_lawyer\n" +
            "    UNION ALL\n" +
            "    SELECT LAWYER_NO, CASE_ID\n" +
            "    FROM plaintiff_lawyer\n" +
            ")\n" +
            "SELECT \n" +
            "    li._id, \n" +
            "    li.LAWYER_NAME, \n" +
            "    li.LAWFIRM AS LAWFIRM, \n" +
            "    li.WIN AS WIN, \n" +
            "    li.LOSE AS LOSE, \n" +
            "    li.COUNT AS COUNT\n" +
            "FROM lawyer_info li\n" +
            "LEFT JOIN LawyerCases lc ON lc.LAWYER_NO = li._id\n" +
            "JOIN RelevantCases rc ON rc.CASE_ID = lc.CASE_ID\n" +
            "WHERE li.LAWYER_NAME != 'None'\n" +
            "GROUP BY li._id, li.LAWYER_NAME, li.LAWFIRM, li.WIN, li.LOSE, li.COUNT\n" +
            "ORDER BY COUNT DESC, WIN DESC\n" +
            "LIMIT 50 OFFSET :page;\n", nativeQuery = true)
    List<LawyerInformationDTO> searchAll(@Param("page") int page);

    /**
     * 전체 변호사 수를 계산
     * 조건: 이름이 'None'이 아니고, 1999년 이후 사건에 참여한 적이 있어야 함
     *
     * @return 조건을 만족하는 변호사 수
     */
    @Query(value = "WITH RelevantCases AS (\n" +
            "    SELECT CASE_ID\n" +
            "    FROM case_info\n" +
            "    WHERE DECISION_DATE >= '1999-01-01'\n" +
            "),\n" +
            "LawyerCases AS (\n" +
            "    SELECT LAWYER_NO\n" +
            "    FROM defendant_lawyer\n" +
            "    UNION ALL\n" +
            "    SELECT LAWYER_NO\n" +
            "    FROM plaintiff_lawyer\n" +
            ")\n" +
            "SELECT COUNT(*)\n" +
            "FROM lawyer_info li\n" +
            "WHERE li.LAWYER_NAME != 'None'\n" +
            "  AND EXISTS (\n" +
            "      SELECT 1\n" +
            "      FROM LawyerCases lc\n" +
            "      JOIN RelevantCases rc ON rc.CASE_ID = lc.LAWYER_NO\n" +
            "      WHERE lc.LAWYER_NO = li._id\n" +
            "  );\n", nativeQuery = true)
    long countAll();
}
