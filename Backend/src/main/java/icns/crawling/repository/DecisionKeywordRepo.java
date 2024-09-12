package icns.crawling.repository;

import icns.crawling.model.DecisionKeywordDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DecisionKeywordRepo extends JpaRepository<DecisionKeywordDTO, Integer> {
    List<DecisionKeywordDTO> findAllByCaseId(int id);

    List<DecisionKeywordDTO> findAllByKeyword(String keyword);

    @Query(value = "WITH FilteredRemedies AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY CASE_ID ORDER BY _id) AS row_num\n" +
            "    FROM DECISION_KEYWORD\n" +
            "    WHERE KEYWORD LIKE CONCAT('%', :keyword, '%')\n" +
            "),\n" +
            "FirstPerCase AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH\n" +
            "    FROM FilteredRemedies\n" +
            "    WHERE row_num = 1\n" +
            "),\n" +
            "PaginatedCases AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH,\n" +
            "        ROW_NUMBER() OVER (ORDER BY CASE_ID) AS global_row_num\n" +
            "    FROM FirstPerCase\n" +
            ")\n" +
            "SELECT\n" +
            "    _id,\n" +
            "    CASE_ID,\n" +
            "    KEYWORD,\n" +
            "    PARAGRAPH\n" +
            "FROM PaginatedCases\n" +
            "WHERE global_row_num BETWEEN 20 * (:page - 1) + 1 AND 20 * :page;", nativeQuery = true)
    List<DecisionKeywordDTO> searchByKeyword(@Param("keyword") String keyword, @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM DECISION_KEYWORD l WHERE KEYWORD LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
    long count(@Param("keyword") String keyword);

    @Query(value = "WITH FilteredRemedies AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY CASE_ID ORDER BY _id) AS row_num\n" +
            "    FROM DECISION_KEYWORD\n" +
            "),\n" +
            "FirstPerCase AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH\n" +
            "    FROM FilteredRemedies\n" +
            "    WHERE row_num = 1\n" +
            "),\n" +
            "PaginatedCases AS (\n" +
            "    SELECT\n" +
            "        _id,\n" +
            "        CASE_ID,\n" +
            "        KEYWORD,\n" +
            "        PARAGRAPH,\n" +
            "        ROW_NUMBER() OVER (ORDER BY CASE_ID) AS global_row_num\n" +
            "    FROM FirstPerCase\n" +
            ")\n" +
            "SELECT\n" +
            "    _id,\n" +
            "    CASE_ID,\n" +
            "    KEYWORD,\n" +
            "    PARAGRAPH\n" +
            "FROM PaginatedCases\n" +
            "WHERE global_row_num BETWEEN 20 * (:page - 1) + 1 AND :page * 20", nativeQuery = true)
    List<DecisionKeywordDTO> searchAll(@Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM DECISION_KEYWORD", nativeQuery = true)
    long countAll();
}
