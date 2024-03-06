package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.base.BaseTimeEntity;
import dragonfly.ews.domain.file.dto.ExcelFileColumnCreateDto;
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
    @JoinColumn(name = "excel_file_id")
    private ExcelMemberFile excelMemberFile;

    public ExcelFileColumn(ExcelFileColumnCreateDto excelFileColumnCreateDto) {
        this.columnName = excelFileColumnCreateDto.getColumnName();
        this.dataType = excelFileColumnCreateDto.getDataType();
    }
}
