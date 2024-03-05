package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.exception.ExtensionNotFoundException;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.dto.MemberFileLogCreateDto;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 맴버가 소유한 파일
 * 1. 파일은 하나의 맴버에게 소유된다.
 * 2. 파일은 최초 저장 및 업데이트시에 FileLog 저장되어야 한다.
 */
@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@Inheritance(strategy = InheritanceType.JOINED)
public class MemberFile extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;
    private String fileName;
    private String fileExtension; // csv, xls

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "memberFile")
    private List<MemberFileLog> memberFileLogs = new ArrayList<>();
    @Lob
    private String description;

    public MemberFile(@NotNull Member owner, @NotNull String submittedFileName,
                      @NotNull String originalFilename, @NotNull String savedFilename) {
        this.fileName = submittedFileName;
        this.fileExtension = getFileExt(originalFilename);
        this.owner = owner;
        getOwner().getMemberFiles().add(this);

        validateSavedFilename(savedFilename);
        updateFile(savedFilename);
    }

    public MemberFile(@NotNull Member owner, @NotNull String originalFilename, @NotNull String savedFilename) {
        this.fileName = originalFilename;
        this.fileExtension = getFileExt(originalFilename);
        this.owner = owner;
        getOwner().getMemberFiles().add(this);

        validateSavedFilename(savedFilename);
        updateFile(savedFilename);
    }

    /**
     * [파일 확장자 얻어내기]
     * <p/> 파일에 확장자가 존재하지 않으면 예외 발생
     *
     * @param fileName
     * @return
     * @throws dragonfly.ews.domain.file.exception.ExtensionNotFoundException
     */
    private String getFileExt(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0) {
            throw new ExtensionNotFoundException("파일 확장자를 찾을 수 없습니다. 파일 확장자가 파일 이름에 포함되어야합니다.");
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * [파일 초기화 및 업데이트 파일명 검사]
     * <p/>
     * 파일의 확장자가 기존의 확장자와 같은지, 파일에 확장자가 존재하는 지 확인.
     * <br/> 파일이 업데이트 및 저장되기 전에 확인되어야 한다.
     * <br/> 파일의 확장자가 기존과 동일하지 않으면 예외 발생.
     *
     * @throws dragonfly.ews.domain.file.exception.ExtensionNotEqualException
     */
    private void validateSavedFilename(String savedFilename) {
        String ext = getFileExt(savedFilename);
        if (!ext.equals(getFileExtension())) {
            throw new ExtensionNotFoundException("파일의 확장자가 동일해야합니다.");
        }
    }

    // ==편의 메소드==

    /**
     * [파일 업데이트 및 로그 엔티티 생성]
     * <br/> MemberFileLog 를 생성할 때 DTO를 사용하지 않아서 제거
     *
     * @param savedFilename
     */
    @Deprecated
    public void updateFile(@NotNull String savedFilename) {
        validateSavedFilename(savedFilename);
        MemberFileLog memberFileLog = new MemberFileLog(this, savedFilename);
        getMemberFileLogs().add(memberFileLog);
    }

    public void updateFile(MemberFileLogCreateDto memberFileLogCreateDto) {
        String savedFileName = memberFileLogCreateDto.getSavedFileName();
        validateSavedFilename(savedFileName);
        MemberFileLog memberFileLog = new MemberFileLog(this, savedFileName);
        memberFileLog.changeDescription(memberFileLogCreateDto.getDescription());
        getMemberFileLogs().add(memberFileLog);
    }

    public void changeProject(@NotNull Project project) {
        if (getProject() != null) {
            getProject().getMemberFiles().remove(this);
            log.info("[MemberFile] 프로젝트와의 연관관계가 제거되었습니다.");
        }
        setProject(project);
        project.getMemberFiles().add(this);
    }

    public void changeDescription(String description) {
        setDescription(description);
    }

    public String getOriginalName() {
        return getFileName() + '.' + getFileExtension();
    }

    public boolean isSameExt(String ext) {
        return getFileExtension().equalsIgnoreCase(ext);
    }

}
