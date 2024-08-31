package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Quote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {

    private Connection c;

    private static final String GET_ONE = "SELECT symbol, open, high, low, price, "
            + "volume, latest_trading_day, previous_close, change, change_percent, "
            + "timestamp FROM quote WHERE symbol = ?";

    private static final String GET_ALL = "SELECT * FROM quote";

    private static final String DELETE_ONE = "DELETE FROM quote WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM quote";

    private static final String INSERT = "INSERT INTO quote (symbol, open, "
            + "high, low, price, volume, latest_trading_day, previous_close, change, "
            + "change_percent, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE quote SET open = ?, high = ?, "
            + "low = ?, price = ?, volume = ?, latest_trading_day = ?, "
            + "previous_close = ?, change = ?, change_percent = ?, timestamp = ? "
            + "WHERE symbol = ?";

    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    public QuoteDao(Connection connection) {
        this.c = connection;
    }

    @Override
    public Quote save(Quote entity) throws IllegalArgumentException {

        if (this.findById(entity.getTicker()).isEmpty()) {
            try (PreparedStatement statement = this.c.prepareStatement(INSERT)) {
                infoLogger.info("Inserting the new quote");
                statement.setString(1, entity.getTicker());
                statement.setDouble(2, entity.getOpen());
                statement.setDouble(3, entity.getHigh());
                statement.setDouble(4, entity.getLow());
                statement.setDouble(5, entity.getPrice());
                statement.setInt(6, entity.getVolume());
                statement.setDate(7, entity.getLatestTradingDay());
                statement.setDouble(8, entity.getPreviousClose());
                statement.setDouble(9, entity.getChange());
                statement.setString(10, entity.getChangePercent());
                statement.setTimestamp(11, entity.getTimestamp());
                statement.execute();
                return entity;
            } catch (SQLException e) {
                errorLogger.error("An SQL Exception had occurred when trying to INSERT a new quote, error message {}", e.getMessage());
            }
        } else {
            try (PreparedStatement statement = this.c.prepareStatement(UPDATE)) {
                infoLogger.info("Updating the quote values");
                statement.setDouble(1, entity.getOpen());
                statement.setDouble(2, entity.getHigh());
                statement.setDouble(3, entity.getLow());
                statement.setDouble(4, entity.getPrice());
                statement.setInt(5, entity.getVolume());
                statement.setDate(6, entity.getLatestTradingDay());
                statement.setDouble(7, entity.getPreviousClose());
                statement.setDouble(8, entity.getChange());
                statement.setString(9, entity.getChangePercent());
                statement.setTimestamp(10, entity.getTimestamp());
                statement.setString(11, entity.getTicker());
                statement.execute();
                return entity;
            } catch (SQLException e) {
                errorLogger.error("An SQL Exception had occurred when trying to UPDATE quote, error message {}", e.getMessage());
            }
        }
        return entity;
    }

    @Override
    public Optional<Quote> findById(String s) throws IllegalArgumentException {
        Quote quote = new Quote();
        try (PreparedStatement statement = this.c.prepareStatement(GET_ONE)) {
            infoLogger.info("Getting one quote by Id");
            statement.setString(1, s);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                quote.setTicker(rs.getString("symbol"));
                quote.setOpen(rs.getDouble("open"));
                quote.setHigh(rs.getDouble("high"));
                quote.setLow(rs.getDouble("low"));
                quote.setPrice(rs.getDouble("price"));
                quote.setVolume(rs.getInt("volume"));
                quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
                quote.setPreviousClose(rs.getDouble("previous_close"));
                quote.setChange(rs.getDouble("change"));
                quote.setChangePercent(rs.getString("change_percent"));
                quote.setTimestamp(rs.getTimestamp("timestamp"));
                return Optional.of(quote);
            }

            return Optional.empty();

        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to SELECT a quote based on Id, error message {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement statement = this.c.prepareStatement(GET_ALL)) {
            infoLogger.info("SELECTING all quotes from DB");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Quote quote = new Quote();
                quote.setTicker(rs.getString("symbol"));
                quote.setOpen(rs.getDouble("open"));
                quote.setHigh(rs.getDouble("high"));
                quote.setLow(rs.getDouble("low"));
                quote.setPrice(rs.getDouble("price"));
                quote.setVolume(rs.getInt("volume"));
                quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
                quote.setPreviousClose(rs.getDouble("previous_close"));
                quote.setChange(rs.getDouble("change"));
                quote.setChangePercent(rs.getString("change_percent"));
                quote.setTimestamp(rs.getTimestamp("timestamp"));
                quotes.add(quote);
            }
        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to SELECT all on quotes, error message {}", e.getMessage());
        }
        return quotes;
    }

    @Override
    public void deleteById(String s) throws IllegalArgumentException {
        try(PreparedStatement statement = this.c.prepareStatement(DELETE_ONE)) {
            infoLogger.info("DELETING a quote based off Id");
            statement.setString(1, s);
            statement.execute();
        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to DELETE a quote based on Id, error message {}", e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try(PreparedStatement statement = this.c.prepareStatement(DELETE_ALL)) {
            infoLogger.info("DELETING all quotes");
            statement.execute();
        } catch (SQLException e) {
            errorLogger.error("An SQL Exception had occurred when trying to DELETE all quotes, error message {}", e.getMessage());
        }
    }

}