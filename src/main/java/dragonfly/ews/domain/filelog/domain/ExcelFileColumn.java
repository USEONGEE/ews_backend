package dragonfly.ews.domain.filelog.domain;

import dragonfly.ews.domain.base.BaseTimeEntity;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
import dragonfly.ews.domain.filelog.domain.ExcelMemberFileLog;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExcelFileColumn extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String columnName;
    private String dataType;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excel_memberfile_log_id")
    private ExcelMemberFileLog excelMemberFileLog;


    public ExcelFileColumn(ExcelFileColumnCreateDto excelFileColumnCreateDto) {
        this.columnName = excelFileColumnCreateDto.getColumnName();
        this.dataType = excelFileColumnCreateDto.getDataType();
    }
}
