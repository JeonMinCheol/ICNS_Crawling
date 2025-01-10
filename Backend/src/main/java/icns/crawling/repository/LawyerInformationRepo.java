package icns.crawling.repository;

import icns.crawling.model.LawyerInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LawyerInformationRepo extends JpaRepository<LawyerInformationDTO, Integer> {
    Optional<LawyerInformationDTO> findByName(String lawyer);

    @Query(value ="SELECT * from lawyer_info where LAWYER_NAME like concat(:lawyer, '%') ORDER BY COUNT DESC, WIN DESC LIMIT 50 OFFSET :page;" , nativeQuery = true)
    List<LawyerInformationDTO> searchLawyerByName(@Param("lawyer") String lawyer, @Param("page") int page);

    @Query(value ="select count(*) from lawyer_info where LAWYER_NAME like concat('%', :lawyer, '%');", nativeQuery = true)
    long count(@Param("lawyer") String lawyer);

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
