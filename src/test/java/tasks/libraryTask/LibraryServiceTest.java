package tasks.libraryTask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryServiceTest {

    private final LibraryService libraryService = new LibraryService();

    // Helper methods to create test data
    private List<Book> createTestBooks() {
        var chapter1 = new Chapter("Introduction", 50);
        var chapter2 = new Chapter("Advanced Topics", 75);
        var chapter3 = new Chapter("Conclusion", 25);
        var chapter4 = new Chapter("Getting Started", 40);
        var chapter5 = new Chapter("Deep Dive", 80);
        var chapter6 = new Chapter("Summary", 30);

        var book1 = new Book("Java Basics", "John Doe",
                LocalDate.of(2020, 1, 15),
                List.of(chapter1, chapter2)); // 125 pages

        var book2 = new Book("Advanced Java", "Jane Smith",
                LocalDate.of(2018, 6, 10),
                List.of(chapter4, chapter5, chapter6)); // 150 pages

        var book3 = new Book("Short Guide", "Bob Wilson",
                LocalDate.of(2022, 3, 5),
                List.of(chapter3)); // 25 pages

        var book4 = new Book("Complete Manual", "Alice Brown",
                LocalDate.of(2019, 11, 20),
                List.of(chapter1, chapter2, chapter4, chapter5)); // 245 pages

        return List.of(book1, book2, book3, book4);
    }

    @Test
    @DisplayName("Should calculate total pages of all books")
    void shouldCalculateTotalPages() {
        // Given
        List<Book> books = createTestBooks();
        // Expected: 125 + 150 + 25 + 245 = 545

        // When
        Integer totalPages = libraryService.calculateTotalPages(books);

        // Then
        assertEquals(545, totalPages, "Should calculate correct total pages");
    }

    @Test
    @DisplayName("Should calculate average pages per book")
    void shouldCalculateAverageBookPages() {
        // Given
        List<Book> books = createTestBooks();
        // Expected: 545 / 4 = 136.25, but method returns Integer so should be 136

        // When
        Integer averagePages = libraryService.calculateAverageBookPages(books);

        // Then
        assertEquals(136, averagePages, "Should calculate correct average pages");
    }

    @Test
    @DisplayName("Should find the longest book page count")
    void shouldFindLongestBook() {
        // Given
        List<Book> books = createTestBooks();
        // Expected: "Complete Manual" with 245 pages

        // When
        Integer longestBookPages = libraryService.findLongestBook(books);

        // Then
        assertEquals(245, longestBookPages, "Should find longest book with 245 pages");
    }

    @Test
    @DisplayName("Should return title of the longest book")
    void shouldGetLongestBookTitle() {
        // Given
        List<Book> books = createTestBooks();

        // When
        String longestBookTitle = libraryService.getLongestBookTitle(books);

        // Then
        assertEquals("Complete Manual", longestBookTitle, "Should return longest book title");
    }

    @Test
    @DisplayName("Should count books by existing author")
    void shouldCountBooksByAuthor() {
        // Given
        List<Book> books = createTestBooks();

        // When
        int johnDoeBooks = libraryService.countBooksByAuthor(books, "John Doe");
        int janeSmithBooks = libraryService.countBooksByAuthor(books, "Jane Smith");
        int nonExistingBooks = libraryService.countBooksByAuthor(books, "Non Existing");

        // Then
        assertEquals(1, johnDoeBooks, "Should find 1 book by John Doe");
        assertEquals(1, janeSmithBooks, "Should find 1 book by Jane Smith");
        assertEquals(0, nonExistingBooks, "Should find 0 books by non-existing author");
    }

    @Test
    @DisplayName("Should find books older than specified years")
    void shouldGetBooksOlderThan() {
        // Given
        List<Book> books = createTestBooks();
        // Books from 2018, 2019, 2020, 2022
        // Older than 3 years (from 2025): 2018, 2019, 2020
        // Older than 5 years (from 2025): 2018, 2019

        // When
        List<Book> booksOlderThan3 = libraryService.getBooksOlderThan(books, 3);
        List<Book> booksOlderThan5 = libraryService.getBooksOlderThan(books, 5);
        List<Book> booksOlderThan10 = libraryService.getBooksOlderThan(books, 10);

        // Then
        assertTrue(booksOlderThan3.size() >= 2, "Should find books older than 3 years");
        assertTrue(booksOlderThan5.size() >= 1, "Should find books older than 5 years");
        assertTrue(booksOlderThan10.isEmpty(), "Should find no books older than 10 years");

        // Check that all returned books are actually old enough
        for (Book book : booksOlderThan3) {
            assertTrue(book.publishDate().isBefore(LocalDate.now().minusYears(3)),
                    "Book should be older than 3 years");
        }
    }

    @Test
    @DisplayName("Should calculate average chapters per book")
    void shouldCalculateAverageChaptersPerBook() {
        // Given
        List<Book> books = createTestBooks();
        // Book1: 2 chapters, Book2: 3 chapters, Book3: 1 chapter, Book4: 4 chapters
        // Average: (2 + 3 + 1 + 4) / 4 = 2.5, as Integer = 2

        // When
        Integer averageChapters = libraryService.calculateAverageChaptersPerBook(books);

        // Then
        assertEquals(2, averageChapters, "Should calculate correct average chapters");
    }

    @Test
    @DisplayName("Should find book with most chapters")
    void shouldFindBookWithMostChapters() {
        // Given
        List<Book> books = createTestBooks();
        // "Complete Manual" has 4 chapters

        // When
        String bookWithMostChapters = libraryService.findBookWithMostChapters(books);

        // Then
        assertEquals("Complete Manual", bookWithMostChapters,
                "Should find book with most chapters");
    }

    @Test
    @DisplayName("Should find shortest book page count")
    void shouldFindShortestBook() {
        // Given
        List<Book> books = createTestBooks();
        // Expected: "Short Guide" with 25 pages

        // When
        Integer shortestBookPages = libraryService.findShortestBook(books);

        // Then
        assertEquals(25, shortestBookPages, "Should find shortest book with 25 pages");
    }

    @Test
    @DisplayName("Should find books published in specific year")
    void shouldGetBooksPublishedInYear() {
        // Given
        List<Book> books = createTestBooks();

        // When
        List<Book> booksFrom2020 = libraryService.getBooksPublishedInYear(books, 2020);
        List<Book> booksFrom2018 = libraryService.getBooksPublishedInYear(books, 2018);
        List<Book> booksFrom2025 = libraryService.getBooksPublishedInYear(books, 2025);

        // Then
        assertEquals(1, booksFrom2020.size(), "Should find 1 book from 2020");
        assertEquals("Java Basics", booksFrom2020.get(0).title(), "Should find correct book from 2020");

        assertEquals(1, booksFrom2018.size(), "Should find 1 book from 2018");
        assertEquals("Advanced Java", booksFrom2018.get(0).title(), "Should find correct book from 2018");

        assertTrue(booksFrom2025.isEmpty(), "Should find no books from 2025");
    }

    @Test
    @DisplayName("Should handle empty book list")
    void shouldHandleEmptyBookList() {
        // Given
        List<Book> emptyBooks = new ArrayList<>();

        // When & Then
        assertEquals(0, libraryService.calculateTotalPages(emptyBooks),
                "Should return 0 for empty list");
        assertEquals(0, libraryService.calculateAverageBookPages(emptyBooks),
                "Should return 0 for empty list");
        assertEquals(0, libraryService.findLongestBook(emptyBooks),
                "Should return 0 for empty list");
        assertEquals("", libraryService.getLongestBookTitle(emptyBooks),
                "Should return empty string for empty list");
        assertEquals(0, libraryService.findShortestBook(emptyBooks),
                "Should return 0 for empty list");
        assertTrue(libraryService.getBooksOlderThan(emptyBooks, 5).isEmpty(),
                "Should return empty list");
        assertEquals(0, libraryService.calculateAverageChaptersPerBook(emptyBooks),
                "Should return 0 for empty list");
        assertEquals("", libraryService.findBookWithMostChapters(emptyBooks),
                "Should return empty string for empty list");
        assertTrue(libraryService.getBooksPublishedInYear(emptyBooks, 2020).isEmpty(),
                "Should return empty list");
    }

    @Test
    @DisplayName("Should handle single book")
    void shouldHandleSingleBook() {
        // Given
        var chapter = new Chapter("Only Chapter", 100);
        var book = new Book("Only Book", "Only Author", LocalDate.of(2020, 1, 1), List.of(chapter));
        List<Book> singleBookList = List.of(book);

        // When & Then
        assertEquals(100, libraryService.calculateTotalPages(singleBookList));
        assertEquals(100, libraryService.calculateAverageBookPages(singleBookList));
        assertEquals(100, libraryService.findLongestBook(singleBookList));
        assertEquals("Only Book", libraryService.getLongestBookTitle(singleBookList));
        assertEquals(100, libraryService.findShortestBook(singleBookList));
        assertEquals(1, libraryService.calculateAverageChaptersPerBook(singleBookList));
        assertEquals("Only Book", libraryService.findBookWithMostChapters(singleBookList));
    }
}