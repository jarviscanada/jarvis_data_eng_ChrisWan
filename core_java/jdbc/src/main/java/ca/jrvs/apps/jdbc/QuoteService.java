package ca.jrvs.apps.jdbc;

import java.util.NoSuchElementException;
import java.util.Optional;

public class QuoteService {

    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
        this.dao = dao;
        this.httpHelper = httpHelper;
    }

    /**
     * Fetches latest quote data from endpoint
     * @param ticker
     * @return Latest quote information or empty optional if ticker symbol not found
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
        Optional<Quote> quote = Optional.of(new Quote());
        try {
            quote = Optional.ofNullable(httpHelper.fetchQuoteInfo(ticker));

            if (dao.findById(ticker).isEmpty() && quote.isPresent()) {
                dao.save(quote.get());
            }

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return quote;
    }

}