package icns.crawling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DECISION_KEYWORD")
public class DecisionKeywordDTO {
    @Id
    @Column(name = "_id")
    private int id;
    @Column(name = "CASE_ID")
    private int caseId;
    @Column(name = "KEYWORD")
    private String keyword;
    @Column(name = "PARAGRAPH")
    private String paragraph;
}
