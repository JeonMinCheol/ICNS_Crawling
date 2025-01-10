package icns.crawling.repository;

import icns.crawling.model.CaseInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


public interface CaseInformationRepo extends JpaRepository<CaseInformationDTO, Integer> {
    @Query(value = "SELECT * FROM case_info WHERE CASE_NAME LIKE CONCAT('%', :caseName, '%') AND DECISION_DATE = :date;", nativeQuery = true)
    List<CaseInformationDTO> findByCaseNameAndDecisionDate(@Param("caseName") String caseName, @Param("date") Date date);
    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *,\n" +
            "           ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num\n" +
            "    FROM case_info" +
            "    WHERE INDEX_NO LIKE CONCAT(:indexNo, '%') \n" +
            ") AS FilteredCases\n" +
            "WHERE global_row_num BETWEEN :count * (:page - 1) + 1 AND :count * :page;\n", nativeQuery = true)
    List<CaseInformationDTO> searchByIndexNo(@Param("indexNo") String indexNo, @Param("page") int page, @Param("count") int count);

    @Query(value = "SELECT COUNT(*) FROM case_info l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%') AND DECISION_DATE > '1998-12-31'", nativeQuery = true)
    long count(@Param("indexNo") String indexNo);

    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *,\n" +
            "           ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num\n" +
            "    FROM case_info\n" +
            ") AS FilteredCases\n" +
            "WHERE global_row_num BETWEEN :count * (:page - 1) + 1 AND :count * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchAll(@Param("page") int page, @Param("count") int count);

    @Query(value = "SELECT COUNT(*) FROM case_info;", nativeQuery = true)
    long countAll();
}
