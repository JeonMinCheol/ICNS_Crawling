package icns.crawling.repository;

import icns.crawling.model.DecisionKeywordDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DecisionKeywordRepo extends JpaRepository<DecisionKeywordDTO, Integer> {
    List<DecisionKeywordDTO> findAllByCaseId(int id);

    List<DecisionKeywordDTO> findAllByKeyword(String keyword);

    @Query(value = "SELECT * FROM DECISION_KEYWORD l WHERE KEYWORD LIKE CONCAT('%', :keyword, '%') LIMIT 20 OFFSET :page", nativeQuery = true)
    List<DecisionKeywordDTO> searchByKeyword(@Param("keyword") String keyword, @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM DECISION_KEYWORD l WHERE KEYWORD LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
    long count(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM DECISION_KEYWORD LIMIT 20 OFFSET :page", nativeQuery = true)
    List<DecisionKeywordDTO> searchAll(@Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM DECISION_KEYWORD", nativeQuery = true)
    long countAll();
}
