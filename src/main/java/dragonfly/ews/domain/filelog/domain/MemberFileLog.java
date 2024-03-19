package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MemberFileLog extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "file_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private MemberFile memberFile;
    private String savedName;
    @Lob
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "memberFileLog")
    private List<AnalysisResult> analysisResults = new ArrayList<>();

    private boolean isValidated = false;


    public MemberFileLog(MemberFile memberFile, String savedName) {
        this.memberFile = memberFile;
        memberFile.getMemberFileLogs().add(this);
        this.savedName = savedName;
    }

    public void changeDescription(String description) {
        setDescription(description);
    }

    public void changeValidated(boolean bool) {
        this.isValidated = bool;
    }

    public void changeMemberFile(MemberFile memberFile) {
        setMemberFile(memberFile);

    }
}
