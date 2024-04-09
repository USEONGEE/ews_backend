package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExcelMemberFileLog extends MemberFileLog {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "excelMemberFileLog")
    private List<ExcelFileColumn> columns = new ArrayList<>();

    // 현재는 모델의 이름만 저장함
    @ElementCollection
    @CollectionTable(name = "AVAILABLE_MODEL", joinColumns = @JoinColumn(name = "excel_member_file_log_id"))
    private Set<String> availableModel = new HashSet<>();

    @Lob
    private String metadata;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "excelMemberFileLog", fetch = FetchType.EAGER)
    private List<ExcelMemberFileLogValidationStep> excelMemberFileLogValidationSteps = new ArrayList<>();

    // TODO 현재 파일에 대한 메타데이터를 저장해야한다. ExcelMemberFileLogMetadata를 만들어서 저장해야한다.

    /**
     * [검증이 완료되었는지 확인]
     *
     * @return
     */
    public boolean isValidated() {
        // 모든 검증이 완료되지 않았다면 false
        if (!(ExcelMemberFileLogValidationType.values().length == excelMemberFileLogValidationSteps.size())) {
            return false;
        }

        for (ExcelMemberFileLogValidationStep step : excelMemberFileLogValidationSteps) {
            switch (step.getStatus()) {
                case SUCCESS:
                    continue;
                case FAIL:
                    throw new IllegalStateException("검증에 실패한 항목이 있습니다.");
                case PROCESSING:
                    return false;
                default:
                    throw new IllegalStateException("Unexpected value: " + step.getStatus());
            }
        }
        return true;
    }

    public ExcelMemberFileLog(MemberFile memberFile, String savedName) {
        super(memberFile, savedName);
    }

    public void changeMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void addColumn(ExcelFileColumn excelFileColumn) {
        columns.add(excelFileColumn);
        excelFileColumn.setExcelMemberFileLog(this);
    }

    public void removeColumn(ExcelFileColumn excelFileColumn) {
        columns.remove(excelFileColumn);
        excelFileColumn.setExcelMemberFileLog(null);
    }
}
