package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.service.PositionService;
import ca.jrvs.apps.jdbc.service.QuoteService;
import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PositionServiceIntegrationTest {
    private static final String apiKey = "f46e6402bamshb3fa3b36bf72984p1ffd83jsnbb4801711afe";
    private static Connection connection;
    private static PositionService positionService;
    private static QuoteService quoteService;

    @BeforeAll
    static void setup() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
                "stock_quote", "postgres", "password");
        connection = dcm.getConnection();
        QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey, new OkHttpClient());
        PositionDao positionDao = new PositionDao(connection);
        QuoteDao quoteDao = new QuoteDao(connection);
        quoteService = new QuoteService(quoteDao, httpHelper);
        positionService = new PositionService(positionDao, quoteService);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void buy_Pass() {
        String ticker = "MSFT";
        int numberOfShares = 1000;
        double price = 170.0;
        Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
        assertNotNull(result);
        assertEquals(ticker, result.getTicker());
    }

    @Test
    void buy_Fail() {
        String ticker = "MSFT";
        int numberOfShares = 999999999;
        double price = 150.0;
        try {
            Position result = positionService.buy(ticker, numberOfShares, numberOfShares*price);
            assertNull(result.getTicker());
        } catch (IllegalArgumentException e){
            assertEquals("Cannot buy more shares than available volume", e.getMessage());
        }
    }

    @Test
    void sell_Pass() {
        String ticker = "MSFT";
        positionService.sell(ticker);
    }

    @Test
    void sell_Fail() {
        String ticker = "GOOG";
        try {
            positionService.sell(ticker);
        } catch (IllegalArgumentException e){
            assertEquals("Cannot sell stock shares you do not own", e.getMessage());
        }
    }
}
