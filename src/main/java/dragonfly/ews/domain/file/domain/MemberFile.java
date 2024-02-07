package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.base.BaseEntity;
import dragonfly.ews.domain.file.exception.ExtensionNotFoundException;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.project.domain.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 맴버가 소유한 파일
 * 1. 파일은 하나의 맴버에게 소유된다.
 * 2. 파일은 최초 저장 및 업데이트시에 FileLog 저장되어야 한다.
 */
@Entity
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFile extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;
    private String fileName;
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "memberFile")
    private List<MemberFileLog> memberFileLogs = new ArrayList<>();

    public MemberFile(Member owner,@NotNull String originalFilename, String savedFilename) {
        this.fileName = originalFilename;
        this.fileType = getFileExt(originalFilename);
        this.owner = owner;
        getOwner().getMemberFiles().add(this);
        updateFile(savedFilename);
    }

    private String getFileExt(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex <  0) {
            throw new ExtensionNotFoundException("파일 확장자를 찾을 수 없습니다. 파일 확장자가 파일 이름에 포함되어야합니다.");
        }
        return fileName.substring(lastDotIndex + 1);
    }

    // ==편의 메소드==
    public void updateFile(@NotNull String savedName) {
        MemberFileLog memberFileLog = new MemberFileLog(this, savedName);
        getMemberFileLogs().add(memberFileLog);
    }

    public void addProject(@NotNull Project project) {
        if (getProject() != null) {
            getProject().getMemberFiles().remove(this);
        }
        setProject(project);
        project.getMemberFiles().add(this);
    }

}
