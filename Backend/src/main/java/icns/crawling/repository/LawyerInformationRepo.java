package icns.crawling.repository;

import icns.crawling.model.LawyerInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LawyerInformationRepo extends JpaRepository<LawyerInformationDTO, Integer> {
    Optional<LawyerInformationDTO> findByName(String lawyer);

    @Query(value = "SELECT * FROM LAWYER_INFORMATION l WHERE NAME LIKE CONCAT('%', :lawyer, '%') ORDER BY COUNT DESC, CASE_WIN DESC LIMIT 20 OFFSET :page", nativeQuery = true)
    List<LawyerInformationDTO> searchLawyerByName(@Param("lawyer") String lawyer, @Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM LAWYER_INFORMATION WHERE NAME LIKE CONCAT('%', :lawyer, '%')", nativeQuery = true)
    long count(@Param("lawyer") String lawyer);

    @Query(value = "SELECT * FROM LAWYER_INFORMATION  ORDER BY COUNT DESC, CASE_WIN DESC LIMIT 20 OFFSET :page", nativeQuery = true)
    List<LawyerInformationDTO> searchAll(@Param("page") int page);

    @Query(value = "SELECT COUNT(*) FROM LAWYER_INFORMATION", nativeQuery = true)
    long countAll();
}
