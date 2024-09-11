package icns.crawling.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LAWYER_INFORMATION")
public class LawyerInformationDTO {
    @Id
    @Column(name = "_id")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "WIN")
    private int win;
    @Column(name = "LOSE")
    private int lose;
    @Column(name = "COUNT")
    private int count;
    @Column(name = "CASE_WIN")
    private int case_win;
    @Column(name = "CASE_LOSE")
    private int case_lose;

}
