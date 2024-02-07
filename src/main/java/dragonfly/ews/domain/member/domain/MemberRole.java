package dragonfly.ews.domain.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberRole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_GUEST;
}
