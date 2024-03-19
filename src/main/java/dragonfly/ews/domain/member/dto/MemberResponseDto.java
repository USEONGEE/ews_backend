package dragonfly.ews.domain.member.dto;

import dragonfly.ews.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    String nickname;
    String email;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
