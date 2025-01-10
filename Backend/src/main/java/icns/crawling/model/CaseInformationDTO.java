package icns.crawling.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_info")
public class CaseInformationDTO {
    @Id
    @Column(name = "CASE_ID")
    private int _id;
    @Column(name = "CASE_NAME")
    private String caseName;
    @Column(name = "CASE_KIND")
    private String caseKind;
    @Column(name = "INCIDENT_REASON")
    private String incidentReason;
    @Column(name = "COURT_NAME")
    private String courtName;
    @Column(name = "INDEX_NO")
    private String indexNo;
    @Column(name = "PLAINTIFF_NAME")
    private String plaintiff;
    @Column(name = "DEFENDANT_NAME")
    private String defendant;
    @Column(name = "SLIPOP_NO")
    private String slipOp;
    @Column(name = "JUDGE_NAME")
    private String judgeName;
    @Column(name = "DECISION_DATE")
    private Date decisionDate;
    @Column(name = "PLAINTIFF_LAWYER_NUM")
    private int plaintiffLawyerNum;
    @Column(name = "DEFENDANT_LAWYER_NUM")
    private int defendantLawyerNum;
    @Column(name = "SUMMARY")
    private String summary;
    @Column(name = "RESULT")
    private String result;
}
