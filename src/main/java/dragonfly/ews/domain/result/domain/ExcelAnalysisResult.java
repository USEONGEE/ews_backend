package dragonfly.ews.domain.result.domain;

import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class ExcelAnalysisResult extends AnalysisResult {

    public ExcelAnalysisResult(MemberFileLog memberFileLog, AnalysisStatus analysisStatus) {
        addMemberFileLog(memberFileLog);
        setAnalysisStatus(analysisStatus);
    }
}
