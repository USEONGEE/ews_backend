package dragonfly.ews.domain.member.dto;

import dragonfly.ews.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [개인 회원이 조회하는 자신의 정보]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageResponseDto {
    String nickname;
    String email;

    public static MyPageResponseDto of(Member member) {
        return MyPageResponseDto.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
