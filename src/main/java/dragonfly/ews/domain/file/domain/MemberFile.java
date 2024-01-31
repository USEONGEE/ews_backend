package dragonfly.ews.domain.file.domain;

import dragonfly.ews.domain.member.domain.Member;
import jakarta.persistence.*;

@Entity
public class MemberFile {
    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;
    private String fileName;
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;
}
