package dragonfly.ews.domain.file.domain;

/**
 * [사용자가 저장한 파일의 확장자]
 */
public enum FileExtension {
    CSV, XLSX, XLS;

    public static FileExtension fromString(String extension) {
        for (FileExtension fileExtension : values()) {
            if (fileExtension.name().equalsIgnoreCase(extension)) {
                return fileExtension;
            }
        }
        throw new IllegalArgumentException("Unknown extension: " + extension);
    }
}