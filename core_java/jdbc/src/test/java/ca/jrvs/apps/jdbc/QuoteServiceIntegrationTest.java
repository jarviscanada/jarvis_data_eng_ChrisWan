package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.service.QuoteService;
import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuoteServiceIntegrationTest {
    private static final String apiKey = "f46e6402bamshb3fa3b36bf72984p1ffd83jsnbb4801711afe";
    private static Connection connection;
    private static QuoteService quoteService;
    private static String stock = "MSFT";
    private static String invalidStock = "TTADFAP";

    @BeforeAll
    static void setup() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
                "stock_quote", "postgres", "password");
        connection = dcm.getConnection();
        QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey, new OkHttpClient());
        QuoteDao quoteDao = new QuoteDao(connection);
        quoteService = new QuoteService(quoteDao, httpHelper);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void fetchQuoteDataFromAPI_Pass() {
        Optional<Quote> fetchedQuote = quoteService.fetchQuoteDataFromAPI(stock);
        assertTrue(fetchedQuote.isPresent());
        assertEquals(stock, fetchedQuote.get().getTicker());
    }

    @Test
    void fetchQuoteDataFromAPI_Fail() {
        try {
            Optional<Quote> fetchedQuote = quoteService.fetchQuoteDataFromAPI(invalidStock);
        } catch (IllegalArgumentException e){
            assertEquals("No data for the symbol was found. Make sure the symbol is valid", e.getMessage());
        }
    }
}
