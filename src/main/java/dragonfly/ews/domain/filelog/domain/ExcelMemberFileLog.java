package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.file.domain.MemberFile;
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
public class ExcelMemberFileLog extends MemberFileLog {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "excelMemberFileLog")
    private List<ExcelFileColumn> columns = new ArrayList<>();


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
