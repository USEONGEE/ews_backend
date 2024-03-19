package dragonfly.ews.common.security.service;

import org.springframework.stereotype.Component;

public class WhiteList {
    public final static String[] WHITE_LIST_ARRAY = {"/", "/css/**", "/images/**", "/js/**", "/favicon.ico",
            "/h2-console/**", "/member/sign-up",};
}
