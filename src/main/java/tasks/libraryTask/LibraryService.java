package tasks.libraryTask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    public Integer calculateTotalPages(List<Book> books) {
        if (books.isEmpty()) {
            return 0;
        } else {
            // loop solution
//            var totalPages = 0;
//            for (Book book : books) {
//                for (Chapter chapter : book.chapters()) {
//                    totalPages += chapter.pages();
//                }
//            }
//            return totalPages;
            // stream solution
            return books.stream().flatMap(book -> book.chapters().stream()).mapToInt(Chapter::pages).sum();
        }
    }

    public Integer calculateAverageBookPages(List<Book> books) {
        if (books.isEmpty()) {
            return 0;
        } else {
            var averageBookPage = 0;
            var totalPages = calculateTotalPages(books);
            averageBookPage = totalPages / books.size();
            return averageBookPage;
        }
    }

    public Integer findLongestBook(List<Book> books) {
        if (books.isEmpty()) {
            return 0;
        } else {
            var longestBook = 0;
            var totalBookSize = 0;
            for (Book book : books) {
                for (Chapter chapter : book.chapters()) {
                    totalBookSize += chapter.pages();
                }
                if (totalBookSize > longestBook) {
                    longestBook = totalBookSize;
                }
                totalBookSize = 0;
            }
            return longestBook;
        }
    }

    public String getLongestBookTitle(List<Book> books) {
        if (books.isEmpty()) {
            return "";
        } else {
            var longestBookTitle = "";
            var numOfLongestBookTitle = 0;
            for (Book book : books) {
                var titleLength = book.title().length();
                if (titleLength > numOfLongestBookTitle) {
                    numOfLongestBookTitle = titleLength;
                    longestBookTitle = book.title();
                }
            }
            return longestBookTitle;
        }
    }

    public Integer countBooksByAuthor(List<Book> books, String authorName) {
        if (books.isEmpty()) {
            return 0;
        } else {
            var count = 0;
            for (Book book : books) {
                if (book.author().equals(authorName)) {
                    count++;
                }
            }
            return count;
        }
    }

    public List<Book> getBooksOlderThan(List<Book> books, int years) {
        List<Book> getBooksResult = new ArrayList<>();
        for (Book book : books) {
            var calculatedYears = LocalDate.now().getYear() - book.publishDate().getYear();
            if (calculatedYears > years) {
                getBooksResult.add(book);
            }
        }
        return getBooksResult;
    }

    public Integer calculateAverageChaptersPerBook(List<Book> books) {
        if (books.isEmpty()) {
            return 0;
        } else {
            var averageChaptersPerBook = 0;
            for (Book book : books) {
                averageChaptersPerBook += book.chapters().size();
            }
            averageChaptersPerBook = averageChaptersPerBook / books.size();
            return averageChaptersPerBook;
        }

    }

    public String findBookWithMostChapters(List<Book> books) {
        if (books.isEmpty()) {
            return "";
        } else {
            var mostChaptersBook = "";
            var numOfMostChaptersBook = 0;
            for (Book book : books) {
                var chapterSize = book.chapters().size();
                if (chapterSize > numOfMostChaptersBook) {
                    numOfMostChaptersBook = chapterSize;
                    mostChaptersBook = book.title();
                }
            }
            return mostChaptersBook;
        }
    }

    public Integer findShortestBook(List<Book> books) {
        if (books.isEmpty()) {
            return 0;
        } else {
            var shortestBook = Integer.MAX_VALUE;
            var totalBookSize = 0;
            for (Book book : books) {
                for (Chapter chapter : book.chapters()) {
                    totalBookSize += chapter.pages();
                }
                if (shortestBook > totalBookSize) {
                    shortestBook = totalBookSize;
                }
                totalBookSize = 0;
            }
            return shortestBook;
        }
    }

    public List<Book> getBooksPublishedInYear(List<Book> books, int year) {
        List<Book> getBooksPublishedInYear = new ArrayList<>();
        for (Book book : books) {
            var bookYear = book.publishDate().getYear();
            if (bookYear == year) {
                getBooksPublishedInYear.add(book);
            }
        }
        return getBooksPublishedInYear;
    }
}
