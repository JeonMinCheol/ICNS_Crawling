package icns.crawling.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailedResponseDTO {
    // CaseInformation
    private String caseName;
    private String courtName;
    private String indexNo;
    private String plaintiff;
    private String defendant;
    private Date decisionDate;
    private String url;

    // Plaintiff's Lawyer
    private List<String> plaintiffLawyerName;
    private int plaintiffLawyerWin;
    private int plaintiffLawyerLose;

    // Defendant's Lawyer
    private List<String> defendantLawyerName;
    private int defendantLawyerWin;
    private int defendantLawyerLose;

    // Decision Keyword
    private List<String> keyword;
    private List<String> paragraph;
}
