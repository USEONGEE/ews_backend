package dragonfly.ews.domain.file.aop.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * [문자열 타입 변환]
 * <br/>주어진 문자열을 정규식을 이용해 다른 타입으로 변환한할 수 있는 지 확인하고 변환해주는 클래스
 * <br/> CsvStrategy, XlsxStrategy 에서 사용됨
 */
@Component
public class DataTypeProvider {
    public String determineDataType(String data) {
        if (data.matches("-?\\d+")) {
            return "Integer";
        } else if (data.matches("-?\\d+(\\.\\d+)?")) {
            return "Double";
        } else if (tryParseDate(data)) {
            return "Date";
        } else {
            return "String";
        }
    }

    private boolean tryParseDate(String data) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate.parse(data, formatter);
                return true;
            } catch (DateTimeParseException e) {
                try {
                    LocalDateTime.parse(data, formatter);
                    return true;
                } catch (DateTimeParseException ex) {
                    // Try next format
                }
            }
        }
        return false;
    }
}
