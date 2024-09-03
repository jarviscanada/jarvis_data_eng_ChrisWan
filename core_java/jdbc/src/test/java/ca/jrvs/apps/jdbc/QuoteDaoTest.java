package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class QuoteDaoTest {
    private static Connection c;
    private static DatabaseConnectionManager dcm;
    private static QuoteDao quoteDao;
    private static PositionDao positionDao;
    private static Quote quote1;
    private static Quote quote2;
    private static Position position1;
    private static Position position2;
    private static String stock1;
    private static String stock2;
    private static Timestamp testTimestamp;

    @BeforeAll
    public static void setup() throws SQLException {
        dcm = new DatabaseConnectionManager("localhost",
                "stock_quote", "postgres", "password");

        c = dcm.getConnection();
        quoteDao = new QuoteDao(c);
        positionDao = new PositionDao(c);

        stock1 = "Stock1";
        stock2 = "Stock2";
        testTimestamp = new Timestamp(System.currentTimeMillis());

        quote1 = new Quote();
        quote1.setTicker(stock1);
        quote1.setOpen(200);
        quote1.setHigh(250);
        quote1.setLow(100);
        quote1.setPrice(150);
        quote1.setVolume(90151616);
        quote1.setLatestTradingDay(new Date(1999, 10, 14));
        quote1.setPreviousClose(150);
        quote1.setChange(0);
        quote1.setChangePercent("0%");
        quote1.setTimestamp(testTimestamp);
        quoteDao.save(quote1);

        quote2 = new Quote();
        quote2.setTicker(stock2);
        quote2.setOpen(300);
        quote2.setHigh(350);
        quote2.setLow(200);
        quote2.setPrice(250);
        quote2.setVolume(123456789);
        quote2.setLatestTradingDay(new Date(2024, 1, 25));
        quote2.setPreviousClose(275);
        quote2.setChange(25);
        quote2.setChangePercent("10%");
        quote2.setTimestamp(testTimestamp);
        quoteDao.save(quote2);

        position1 = new Position();
        position1.setTicker(stock1);
        position1.setNumOfShares(100);
        position1.setValuePaid(180);
        positionDao.save(position1);

        position2 = new Position();
        position2.setTicker(stock2);
        position2.setNumOfShares(200);
        position2.setValuePaid(250);
        positionDao.save(position2);

    }

    @AfterAll
    static void tearDown() throws SQLException {
        positionDao.deleteById(stock1);
        positionDao.deleteById(stock2);
        quoteDao.deleteById(stock1);
        quoteDao.deleteById(stock2);
        c.close();
    }

    @Test
    void testInsertQuote() {
        Quote testQuote = new Quote();
        String stock = "Stock3";
        quote2.setTicker(stock);
        quote2.setOpen(300);
        quote2.setHigh(350);
        quote2.setLow(200);
        quote2.setPrice(250);
        quote2.setVolume(123456789);
        quote2.setLatestTradingDay(new Date(2024, 1, 25));
        quote2.setPreviousClose(275);
        quote2.setChange(25);
        quote2.setChangePercent("10%");
        quote2.setTimestamp(testTimestamp);
        Quote insertedQuote = quoteDao.save(testQuote);

        assertEquals(testQuote, insertedQuote);
        quoteDao.deleteById(stock);
    }

    @Test
    void testUpdateQuote() {
        quote1.setHigh(1000);
        quoteDao.save(quote1);

        assertTrue(quoteDao.findById(stock1).isPresent());
        assertEquals(1000, quoteDao.findById(stock1).get().getHigh());
    }

    @Test
    void testFindById() {
        assertTrue(quoteDao.findById(stock1).isPresent());
        assertTrue(quoteDao.findById(stock2).isPresent());
    }

    @Test
    void testFindAll() {
        Iterable<Quote> quotes = quoteDao.findAll();
        for (Quote q : quotes) {
            if (q.getTicker().equals(stock1)) {
                assertEquals(stock1, q.getTicker());
            }else if (q.getTicker().equals(stock2)){
                assertEquals(stock2, q.getTicker());
            }
        }
    }

    @Test
    void testDeleteById() {
        positionDao.deleteAll();
        quoteDao.deleteById(stock1);
        assertTrue(quoteDao.findById(stock1).isEmpty());
    }

    @Test
    void testDeleteAll() {
        positionDao.deleteAll();
        quoteDao.deleteAll();
        assertTrue(quoteDao.findById(stock1).isEmpty());
        assertTrue(quoteDao.findById(stock2).isEmpty());
    }
}
