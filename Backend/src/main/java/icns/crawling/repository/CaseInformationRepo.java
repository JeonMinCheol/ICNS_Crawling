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
    @Query(value = "SELECT * FROM TEST_CASE l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%') LIMIT 20 OFFSET :page", nativeQuery = true)
    List<CaseInformationDTO> searchByIndexNo(@Param("indexNo") String indexNo, @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM TEST_CASE l WHERE INDEX_NO LIKE CONCAT('%', :indexNo, '%')", nativeQuery = true)
    long count(@Param("indexNo") String indexNo);

    @Query(value = "SELECT * FROM TEST_CASE LIMIT 20 OFFSET :page", nativeQuery = true)
    List<CaseInformationDTO> searchAll( @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM TEST_CASE", nativeQuery = true)
    long countAll();
}
