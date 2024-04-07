package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.develop.aop.LogMethodParams;
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

    // TODO 현재 log가 어디까지 validation 이 되었는지를 저장하는 enum이 있어야 한다.


    public ExcelMemberFileLog(MemberFile memberFile, String savedName) {
        super(memberFile, savedName);
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
