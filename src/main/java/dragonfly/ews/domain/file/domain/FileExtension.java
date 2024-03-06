package dragonfly.ews.domain.file.domain;

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