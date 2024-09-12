package icns.crawling.repository;

import icns.crawling.model.CaseInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


public interface CaseInformationRepo extends JpaRepository<CaseInformationDTO, Integer> {
    Optional<CaseInformationDTO> findByCaseNameAndDecisionDate(String caseName, Date date);
    List<CaseInformationDTO> findAllByIndexNo(String index);
    @Query(value = "WITH FilteredRemedies AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL, \n" +
            "                                ROW_NUMBER() OVER (PARTITION BY INDEX_NO ORDER BY _id) AS row_num \n" +
            "                            FROM CASE_INFO \n" +
            "                            WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%')\n" +
            "                        ), \n" +
            "                        FirstPerCase AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL\n" +
            "                            FROM FilteredRemedies \n" +
            "                            WHERE row_num = 1 \n" +
            "                        ), \n" +
            "                        PaginatedCases AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL, \n" +
            "                                ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num \n" +
            "                            FROM FirstPerCase \n" +
            "                        ) \n" +
            "                        SELECT \n" +
            "                             _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL\n" +
            "                        FROM PaginatedCases \n" +
            "                        WHERE global_row_num BETWEEN 20 * (:page - 1) AND 20 * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchByIndexNo(@Param("indexNo") String indexNo, @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM CASE_INFO l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%')", nativeQuery = true)
    long count(@Param("indexNo") String indexNo);

    @Query(value = "WITH FilteredRemedies AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL, \n" +
            "                                ROW_NUMBER() OVER (PARTITION BY INDEX_NO ORDER BY _id) AS row_num \n" +
            "                            FROM CASE_INFO \n" +
            "                        ), \n" +
            "                        FirstPerCase AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL\n" +
            "                            FROM FilteredRemedies \n" +
            "                            WHERE row_num = 1 \n" +
            "                        ), \n" +
            "                        PaginatedCases AS ( \n" +
            "                            SELECT \n" +
            "                                _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL, \n" +
            "                                ROW_NUMBER() OVER (ORDER BY DECISION_DATE) AS global_row_num \n" +
            "                            FROM FirstPerCase \n" +
            "                        ) \n" +
            "                        SELECT \n" +
            "                             _id, \n" +
            "                                CASE_NAME, \n" +
            "                                COURT_NAME, \n" +
            "                                INDEX_NO, \n" +
            "                                PLAINTIFF, \n" +
            "                                DEFENDANT, \n" +
            "                                DECISION_DATE, \n" +
            "                                URL\n" +
            "                        FROM PaginatedCases \n" +
            "                        WHERE global_row_num BETWEEN 20 * (:page - 1) AND 20 * :page;", nativeQuery = true)
    List<CaseInformationDTO> searchAll( @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM CASE_INFO", nativeQuery = true)
    long countAll();
}
