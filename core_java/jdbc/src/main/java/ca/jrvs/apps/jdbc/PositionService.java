package ca.jrvs.apps.jdbc;

import java.util.Optional;

public class PositionService {

    private PositionDao dao;
    private QuoteService quoteService;

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
            throw new RuntimeException(e);
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
            throw new IllegalArgumentException("Cannot sell stock shares you do not own");
        } else {
            dao.deleteById(ticker);
        }
    }
}