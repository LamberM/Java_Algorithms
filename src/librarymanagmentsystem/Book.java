package librarymanagmentsystem;

import java.time.LocalDate;
import java.util.List;

public record Book(String title, String author, LocalDate publishDate, List<Chapter> chapters) {
}
