package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PositionService {

    private PositionDao dao;
    private QuoteService quoteService;
    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    public PositionService(PositionDao dao, QuoteService quoteService) {
        this.dao = dao;
        this.quoteService = quoteService;
    }

    /**
     * Processes a buy order and updates the database accordingly
     * @param ticker
     * @param numberOfShares
     * @param price
     * @return The position in our database after processing the buy
     */
    public Position buy(String ticker, int numberOfShares, double price) {
        infoLogger.info("Buying a position");
        Optional<Position> position = Optional.of(new Position());
        Position newPosition = new Position();
        try {
            Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);
            if (quote.isEmpty()) {
                throw new IllegalArgumentException("Unable to find ticket " + ticker + " within Quote");
            }

            if (numberOfShares > quote.get().getVolume()) {
                throw new IllegalArgumentException("You cannot buy more shares than the available volume");
            }

            position = dao.findById(ticker);
            if (position.isEmpty()) {
                newPosition.setTicker(ticker);
                newPosition.setNumOfShares(numberOfShares);
                newPosition.setValuePaid(price);
            } else {
                newPosition.setTicker(ticker);
                newPosition.setNumOfShares(position.get().getNumOfShares() + numberOfShares);
                newPosition.setValuePaid(position.get().getValuePaid() + (numberOfShares*price));
            }

        } catch (IllegalArgumentException e) {
            errorLogger.error("Illegal Argument Exception on {} in buy Position", e.getMessage());
        }

        return dao.save(newPosition);
    }

    /**
     * Sells all shares of the given ticker symbol
     * @param ticker
     */
    public void sell(String ticker) {
        Optional<Position> position = dao.findById(ticker);
        if (position.isEmpty()) {
            errorLogger.error("Illegal Argument Exception, cannot sell stock shares you do not own");
            throw new IllegalArgumentException("Cannot sell stock shares you do not own");
        } else {
            dao.deleteById(ticker);
        }
    }

    public Iterable<Position> displayAllRecords() {
        return dao.findAll();
    }
}