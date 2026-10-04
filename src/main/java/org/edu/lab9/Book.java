package org.edu.lab9;

/**
 * Книга библиотечного каталога.
 */
public class Book {
    private final String author;
    private final String title;
    private final int year;

    /**
     * Создаёт описание книги.
     *
     * @param author автор книги
     * @param title  название книги
     * @param year   год издания
     */
    public Book(String author, String title, int year) {
        this.author = author;
        this.title = title;
        this.year = year;
    }

    /**
     * Возвращает автора книги.
     *
     * @return автор
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Возвращает название книги.
     *
     * @return название
     */
    public String getTitle() {
        return title;
    }

    /**
     * Возвращает год издания.
     *
     * @return год издания
     */
    public int getYear() {
        return year;
    }
}
