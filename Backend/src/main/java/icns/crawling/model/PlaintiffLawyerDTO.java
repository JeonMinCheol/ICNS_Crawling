package icns.crawling.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "plaintiff_lawyer")
public class PlaintiffLawyerDTO {
    @Id
    @Column(name = "_id")
    private int id;
    @Column(name = "CASE_ID")
    private int caseId;
    @Column(name = "LAWYER_NO")
    private int lawyerNo;
    @Column(name = "LAWYER_NAME")
    private String name;
    @Column(name = "RESULT")
    private String result;
}
