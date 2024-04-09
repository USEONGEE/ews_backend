package dragonfly.ews.domain.filelog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExcelMemberFileLogValidationStep {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private ExcelMemberFileLogValidationType type;
    @Enumerated(value = EnumType.STRING)
    private ExcelMemberFileLogValidationStatus status;
    private String errorMessage;
    private int errorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excel_member_file_log_id")
    private ExcelMemberFileLog excelMemberFileLog;

    public ExcelMemberFileLogValidationStep(ExcelMemberFileLogValidationType type) {
        this.status = ExcelMemberFileLogValidationStatus.PROCESSING;
        this.type = type;
    }

    /**
     * [검증 성공 시 호출]
     */
    public void validationSuccess() {
        this.status = ExcelMemberFileLogValidationStatus.SUCCESS;
    }

    /**
     * [검증 실패 시 호출]
     * @param errorMessage
     */
    public void validationFail(String errorMessagem, int errorCode) {
        this.errorMessage = errorMessage;
        this.status = ExcelMemberFileLogValidationStatus.FAIL;
    }

}
