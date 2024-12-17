package icns.crawling.repository;

import icns.crawling.model.CaseInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


public interface CaseInformationRepo extends JpaRepository<CaseInformationDTO, Integer> {
    @Query(value = "SELECT * FROM CASE_INFO WHERE CASE_NAME = :caseName AND DECISION_DATE = :date Limit 1;", nativeQuery = true)
    Optional<CaseInformationDTO> findByCaseNameAndDecisionDate(@Param("caseName") String caseName, @Param("date") Date date);
    @Query(value = "WITH FilteredRemedies AS ( \n" +
            "                            SELECT *, \n" +
            "                            ROW_NUMBER() OVER (PARTITION BY INDEX_NO ORDER BY CASE_ID) AS row_num \n" +
            "                            FROM CASE_INFO \n" +
            "                            WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%') AND DECISION_DATE > '1998-12-31'\n" +
            "                        ), \n" +
            "                        FirstPerCase AS ( \n" +
            "                            SELECT * \n" +
            "                            FROM FilteredRemedies \n" +
            "                            WHERE row_num = 1 \n" +
            "                        ), \n" +
            "                        PaginatedCases AS ( \n" +
            "                            SELECT *, \n" +
            "                            ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num \n" +
            "                            FROM FirstPerCase \n" +
            "                        ) \n" +
            "                        SELECT * \n" +
            "                        FROM PaginatedCases \n" +
            "                        WHERE global_row_num BETWEEN :count * (:page - 1) AND :count * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchByIndexNo(@Param("indexNo") String indexNo, @Param("page") int page, @Param("count") int count);

    @Query(value = "SELECT COUNT(*) FROM CASE_INFO l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%') AND DECISION_DATE > '1998-12-31'", nativeQuery = true)
    long count(@Param("indexNo") String indexNo);

    @Query(value = "WITH FilteredRemedies AS ( \n" +
            "                            SELECT *, \n" +
            "                                ROW_NUMBER() OVER (PARTITION BY INDEX_NO ORDER BY CASE_ID) AS row_num AND DECISION_DATE > '1998-12-31' \n" +
            "                            FROM CASE_INFO \n" +
            "                        ), \n" +
            "                        FirstPerCase AS ( \n" +
            "                            SELECT * \n" +
            "                            FROM FilteredRemedies \n" +
            "                            WHERE row_num = 1 \n" +
            "                        ), \n" +
            "                        PaginatedCases AS ( \n" +
            "                            SELECT *, \n" +
            "                                ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num \n" +
            "                            FROM FirstPerCase \n" +
            "                        ) \n" +
            "                        SELECT * \n" +
            "                        FROM PaginatedCases \n" +
            "                        WHERE global_row_num BETWEEN :count * (:page - 1) AND :count * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchAll(@Param("page") int page, @Param("count") int count);

    @Query(value = "SELECT COUNT(*) FROM CASE_INFO WHERE DECISION_DATE > '1998-12-31'", nativeQuery = true)
    long countAll();
}
