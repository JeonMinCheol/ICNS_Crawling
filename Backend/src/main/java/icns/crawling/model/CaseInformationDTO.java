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
@Table(name = "CASE_INFO")
public class CaseInformationDTO {
    @Id
    private int _id;
    @Column(name = "CASE_NAME")
    private String caseName;
    @Column(name = "COURT_NAME")
    private String courtName;
    @Column(name = "INDEX_NO")
    private String indexNo;
    @Column(name = "PLAINTIFF")
    private String plaintiff;
    @Column(name = "DEFENDANT")
    private String defendant;
    @Column(name = "DECISION_DATE")
    private Date decisionDate;
    @Column(name = "URL")
    private String  url;
}
