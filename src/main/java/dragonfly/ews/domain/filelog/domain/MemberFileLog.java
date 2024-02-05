package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.result.domain.FileAnalysisResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFileLog extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "file_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private MemberFile memberFile;
    private String savedName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "memberFileLog")
    private List<FileAnalysisResult> fileAnalysisResults = new ArrayList<>();

    public MemberFileLog(MemberFile memberFile, String savedName) {
        this.memberFile = memberFile;
        this.savedName = savedName;
    }

    // ==편의 메소드==
    public void addFileAnalysisResult(String savedName) {
        FileAnalysisResult fileAnalysisResult = new FileAnalysisResult(this, savedName);
        getFileAnalysisResults().add(fileAnalysisResult);
    }

}
