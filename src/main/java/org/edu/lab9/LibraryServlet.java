package org.edu.lab9;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет библиотечного каталога.
 * <p>
 * Параметры инициализации (задаются в web.xml): {@code libraryName} — название библиотеки,
 * {@code maxRows} — максимальное число строк в таблице.
 * Динамические параметры запроса: {@code author} — фильтр по автору,
 * {@code year} — минимальный год издания.
 */
public class LibraryServlet extends HttpServlet {
    private final List<Book> books = new ArrayList<>();
    private String libraryName;
    private int maxRows;

    /**
     * Читает параметры инициализации и заполняет каталог книгами.
     */
    @Override
    public void init() {
        libraryName = getInitParameter("libraryName");
        if (libraryName == null) {
            libraryName = "Библиотека";
        }
        String rows = getInitParameter("maxRows");
        maxRows = rows == null ? 10 : Integer.parseInt(rows);

        books.add(new Book("Булгаков", "Мастер и Маргарита", 1967));
        books.add(new Book("Достоевский", "Преступление и наказание", 1866));
        books.add(new Book("Толстой", "Война и мир", 1869));
        books.add(new Book("Толстой", "Анна Каренина", 1877));
        books.add(new Book("Пелевин", "Чапаев и Пустота", 1996));
        books.add(new Book("Гоголь", "Мёртвые души", 1842));
    }

    /**
     * Формирует HTML-страницу со списком книг, удовлетворяющих параметрам запроса.
     *
     * @param req  HTTP-запрос
     * @param resp HTTP-ответ
     * @throws ServletException при ошибке работы сервлета
     * @throws IOException      при ошибке ввода-вывода
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String author = req.getParameter("author");
        int minYear = 0;
        String year = req.getParameter("year");
        if (year != null && !year.isBlank()) {
            try {
                minYear = Integer.parseInt(year.trim());
            } catch (NumberFormatException e) {
                minYear = 0;
            }
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        out.println("<title>" + escape(libraryName) + "</title></head>");
        out.println("<body style='font-family:Arial;margin:30px'>");
        out.println("<h1>" + escape(libraryName) + "</h1>");
        out.println("<p>Фильтр: автор = <b>" + (author == null || author.isBlank() ? "любой" : escape(author))
                + "</b>, год издания от <b>" + minYear + "</b></p>");
        out.println("<table border='1' cellpadding='6' style='border-collapse:collapse'>");
        out.println("<tr><th>Автор</th><th>Название</th><th>Год</th></tr>");
        int shown = 0;
        for (Book b : books) {
            boolean byAuthor = author == null || author.isBlank() || b.getAuthor().equalsIgnoreCase(author.trim());
            if (byAuthor && b.getYear() >= minYear && shown < maxRows) {
                out.println("<tr><td>" + b.getAuthor() + "</td><td>" + b.getTitle()
                        + "</td><td>" + b.getYear() + "</td></tr>");
                shown++;
            }
        }
        out.println("</table>");
        out.println("<p>Найдено книг: " + shown + "</p>");
        out.println("<p><a href='index.html'>Новый запрос</a></p>");
        out.println("</body></html>");
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
