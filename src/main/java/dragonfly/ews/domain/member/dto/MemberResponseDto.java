package dragonfly.ews.domain.member.dto;

import dragonfly.ews.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * [다른 회원이 조회하는 정보]
 */
@Data
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String imageName;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageName(member.getProfileImage())
                .build();
    }
}
