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
    private String caseKind;
    private String incidentReason;
    private String indexNo;
    private String plaintiff;
    private String defendant;
    private String slipOp;
    private String judgeName;
    private Date decisionDate;
    private String summary;

    // Plaintiff's Lawyer
    private List<String> plaintiffLawyerName;
    private int plaintiffLawyerNum;

    // Defendant's Lawyer
    private List<String> defendantLawyerName;
    private int defendantLawyerNum;

    // Decision Keyword
    private List<String> sentences;
    private List<String> paragraphs;
    private String result;
}
