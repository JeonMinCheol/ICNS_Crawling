package icns.crawling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_decision")
public class CaseDecisionDTO {
    @Id
    @Column(name = "_id")
    private int id;
    @Column(name = "CASE_ID")
    private int caseId;
    @Column(name = "SENTENCE")
    private String sentence;
    @Column(name = "PARAGRAPH")
    private String paragraph;
}
