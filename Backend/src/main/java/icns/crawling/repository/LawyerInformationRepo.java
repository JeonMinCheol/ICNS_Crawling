package icns.crawling.repository;

import icns.crawling.model.LawyerInformationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LawyerInformationRepo extends JpaRepository<LawyerInformationDTO, Integer> {
    Optional<LawyerInformationDTO> findByName(String lawyer);

    @Query(value ="SELECT \n" +
            "    * \n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        li._id, \n" +
            "        li.LAWYER_NAME, \n" +
            "        li.LAWFIRM AS LAWFIRM, \n" +
            "        li.WIN AS WIN, \n" +
            "        li.LOSE AS LOSE, \n" +
            "        li.COUNT AS COUNT \n" +
            "    FROM \n" +
            "        lawyer_info li\n" +
            "    LEFT JOIN  \n" +
            "        defendant_lawyer dl ON dl.LAWYER_NO = li._id \n" +
            "    LEFT JOIN  \n" +
            "        plaintiff_lawyer pl ON pl.LAWYER_NO = li._id \n" +
            "    JOIN  \n" +
            "        case_info ci \n" +
            "        ON ci.CASE_ID = pl.CASE_ID \n" +
            "        OR ci.CASE_ID = dl.CASE_ID\n" +
            "    WHERE \n" +
            "        ci.decision_date >= '1999-01-01' \n" +
            "        AND li.LAWYER_NAME LIKE CONCAT('%', :lawyer, '%')" +
            "    GROUP BY \n" +
            "        li._id, li.LAWYER_NAME\n" +
            ") AS aggregated_results\n" +
            "ORDER BY \n" +
            "    COUNT DESC, WIN DESC \n" +
            "LIMIT 50 OFFSET :page;" , nativeQuery = true)
    List<LawyerInformationDTO> searchLawyerByName(@Param("lawyer") String lawyer, @Param("page") int page);

    @Query(value ="SELECT  \n" +
            "    COUNT(*)  \n" +
            "FROM ( \n" +
            "    SELECT  \n" +
            "        li._id,  \n" +
            "        li.LAWYER_NAME,  \n" +
            "        li.LAWFIRM AS LAWFIRM,  \n" +
            "        li.WIN AS WIN,  \n" +
            "        li.LOSE AS LOSE,  \n" +
            "        li.COUNT AS COUNT  \n" +
            "    FROM  \n" +
            "        lawyer_info li\n" +
            "    LEFT JOIN  \n" +
            "        defendant_lawyer dl ON dl.LAWYER_NO = li._id \n" +
            "    LEFT JOIN  \n" +
            "        plaintiff_lawyer pl ON pl.LAWYER_NO = li._id \n" +
            "    JOIN  \n" +
            "        case_info ci \n" +
            "        ON ci.CASE_ID = pl.CASE_ID \n" +
            "        OR ci.CASE_ID = dl.CASE_ID\n" +
            "    WHERE  \n" +
            "        ci.decision_date >= '1999-01-01' \n" +
            "        AND li.LAWYER_NAME LIKE CONCAT('%', :lawyer, '%')" +
            "    GROUP BY  \n" +
            "        li._id, li.LAWYER_NAME \n" +
            ") AS aggregated_results;", nativeQuery = true)
    long count(@Param("lawyer") String lawyer);

    @Query(value = "SELECT \n" +
            "    * \n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        li._id, \n" +
            "        li.LAWYER_NAME, \n" +
            "        li.LAWFIRM AS LAWFIRM, \n" +
            "        li.WIN AS WIN, \n" +
            "        li.LOSE AS LOSE, \n" +
            "        li.COUNT AS COUNT \n" +
            "    FROM \n" +
            "        lawyer_info li\n" +
            "    LEFT JOIN  \n" +
            "        defendant_lawyer dl ON dl.LAWYER_NO = li._id \n" +
            "    LEFT JOIN  \n" +
            "        plaintiff_lawyer pl ON pl.LAWYER_NO = li._id \n" +
            "    JOIN  \n" +
            "        case_info ci \n" +
            "        ON ci.CASE_ID = pl.CASE_ID \n" +
            "        OR ci.CASE_ID = dl.CASE_ID\n" +
            "    WHERE \n" +
            "        ci.decision_date >= '1999-01-01' \n" +
            "    GROUP BY \n" +
            "        li._id, li.LAWYER_NAME\n" +
            ") AS aggregated_results\n" +
            "ORDER BY \n" +
            "    COUNT DESC, WIN DESC \n" +
            "LIMIT 50 OFFSET :page;", nativeQuery = true)
    List<LawyerInformationDTO> searchAll(@Param("page") int page);

    @Query(value = "SELECT \n" +
            "    COUNT(*) \n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        li._id, \n" +
            "        li.LAWYER_NAME, \n" +
            "        li.LAWFIRM AS LAWFIRM, \n" +
            "        li.WIN AS WIN, \n" +
            "        li.LOSE AS LOSE, \n" +
            "        li.COUNT AS COUNT \n" +
            "    FROM \n" +
            "        lawyer_info li\n" +
            "    LEFT JOIN  \n" +
            "        defendant_lawyer dl ON dl.LAWYER_NO = li._id \n" +
            "    LEFT JOIN  \n" +
            "        plaintiff_lawyer pl ON pl.LAWYER_NO = li._id \n" +
            "    JOIN  \n" +
            "        case_info ci \n" +
            "        ON ci.CASE_ID = pl.CASE_ID \n" +
            "        OR ci.CASE_ID = dl.CASE_ID\n" +
            "    WHERE \n" +
            "        ci.decision_date >= '1999-01-01' \n" +
            "    GROUP BY \n" +
            "        li._id, li.LAWYER_NAME\n" +
            ") AS aggregated_results;", nativeQuery = true)
    long countAll();
}
