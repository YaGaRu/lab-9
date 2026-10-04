package org.edu.lab9;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты сервлета {@link LibraryServlet} с использованием mock-объектов Mockito.
 * Контейнер сервлетов не запускается: запрос, ответ и конфигурация сервлета заменены заглушками.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LibraryServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;

    private LibraryServlet servlet;
    private StringWriter body;

    @BeforeEach
    void setUp() throws Exception {
        // Параметры инициализации, которые в приложении задаются в web.xml
        when(config.getInitParameter("libraryName")).thenReturn("Тестовая библиотека");
        when(config.getInitParameter("maxRows")).thenReturn("10");
        servlet = new LibraryServlet();
        servlet.init(config);

        // Тело ответа перехватывается в StringWriter
        body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
    }

    private String render(String author, String year) throws Exception {
        when(request.getParameter("author")).thenReturn(author);
        when(request.getParameter("year")).thenReturn(year);
        servlet.doGet(request, response);
        return body.toString();
    }

    @Test
    @DisplayName("Без параметров выводятся все книги каталога")
    void allBooksWithoutParameters() throws Exception {
        String html = render(null, null);
        assertTrue(html.contains("Найдено книг: 6"));
        assertTrue(html.contains("Мастер и Маргарита"));
    }

    @Test
    @DisplayName("Фильтр по автору оставляет только его книги")
    void filterByAuthor() throws Exception {
        String html = render("Толстой", null);
        assertTrue(html.contains("Война и мир"));
        assertTrue(html.contains("Анна Каренина"));
        assertFalse(html.contains("Мастер и Маргарита"));
        assertTrue(html.contains("Найдено книг: 2"));
    }

    @Test
    @DisplayName("Фильтр по минимальному году издания")
    void filterByYear() throws Exception {
        String html = render(null, "1960");
        assertTrue(html.contains("Чапаев и Пустота"));
        assertTrue(html.contains("Мастер и Маргарита"));
        assertFalse(html.contains("Война и мир"));
        assertTrue(html.contains("Найдено книг: 2"));
    }

    @Test
    @DisplayName("Комбинация параметров author и year")
    void filterByAuthorAndYear() throws Exception {
        String html = render("Толстой", "1870");
        assertTrue(html.contains("Анна Каренина"));
        assertFalse(html.contains("Война и мир"));
        assertTrue(html.contains("Найдено книг: 1"));
    }

    @Test
    @DisplayName("Некорректный год игнорируется")
    void invalidYearIsIgnored() throws Exception {
        String html = render(null, "abc");
        assertTrue(html.contains("Найдено книг: 6"));
    }

    @Test
    @DisplayName("Несуществующий автор — пустой результат")
    void unknownAuthor() throws Exception {
        String html = render("Пушкин", null);
        assertTrue(html.contains("Найдено книг: 0"));
    }

    @Test
    @DisplayName("Название библиотеки берётся из параметра инициализации")
    void libraryNameFromInitParameter() throws Exception {
        String html = render(null, null);
        assertTrue(html.contains("<h1>Тестовая библиотека</h1>"));
    }

    @Test
    @DisplayName("Параметр author экранируется в HTML")
    void authorIsEscaped() throws Exception {
        String html = render("<script>", null);
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    @DisplayName("Устанавливается тип содержимого text/html в кодировке UTF-8")
    void contentTypeIsSet() throws Exception {
        render(null, null);
        verify(response, times(1)).setContentType("text/html;charset=UTF-8");
    }

    @Test
    @DisplayName("Число строк ограничено параметром инициализации maxRows")
    void maxRowsLimit() throws Exception {
        when(config.getInitParameter("maxRows")).thenReturn("2");
        LibraryServlet limited = new LibraryServlet();
        limited.init(config);
        when(request.getParameter("author")).thenReturn(null);
        when(request.getParameter("year")).thenReturn(null);
        limited.doGet(request, response);
        assertTrue(body.toString().contains("Найдено книг: 2"));
    }
}
