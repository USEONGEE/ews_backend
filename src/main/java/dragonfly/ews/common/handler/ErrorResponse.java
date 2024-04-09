package dragonfly.ews.common.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int code; // 사전 정의된 에러 코드
    private final String msg;
    private final Object data;

    public static ErrorResponse of(int code, String msg, Object data) {
        return new ErrorResponse(code, msg, data);
    }
}
