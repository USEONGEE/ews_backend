package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
public class ExcelMemberFile extends MemberFile {
    @OneToMany(mappedBy = "excelMemberFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExcelFileColumn> columns = new ArrayList<>();

    public ExcelMemberFile(@NotNull Member owner, @NotNull String submittedFileName,
                           @NotNull String originalFilename, @NotNull String savedFilename) {
        super(owner, submittedFileName, originalFilename, savedFilename);
    }

    public void addColumn(ExcelFileColumn column) {
        columns.add(column);
        column.setExcelMemberFile(this);
    }

    public void removeColumn(ExcelFileColumn column) {
        columns.remove(column);
        column.setExcelMemberFile(null);
    }
}
