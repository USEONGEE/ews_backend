package dragonfly.ews.domain.filelog.domain;

public enum SingleColumnTransformMethod {
    SQUARE, SQUARE_ROOT, COMMERCIAL_LOG, NATURAL_LOG;

    public static SingleColumnTransformMethod fromString(String method) {
        for (SingleColumnTransformMethod singleColumnModifyMethod : values()) {
            if (singleColumnModifyMethod.name().equalsIgnoreCase(method)) {
                return singleColumnModifyMethod;
            }
        }
        throw new IllegalArgumentException("Unknown extension: " + method);
    }
}
