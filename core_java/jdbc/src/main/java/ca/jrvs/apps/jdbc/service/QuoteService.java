package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.NoSuchElementException;
import java.util.Optional;

public class QuoteService {

    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;
    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

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
        infoLogger.info("Fetching Quote Data From API");
        Optional<Quote> quote = Optional.of(new Quote());
        try {
            quote = Optional.ofNullable(httpHelper.fetchQuoteInfo(ticker));

            if (dao.findById(ticker).isEmpty() && quote.isPresent()) {
                dao.save(quote.get());
            }

        } catch (NoSuchElementException e) {
            errorLogger.error("No such element exception on {} in fetchQuoteDataFromAPI", e.getMessage());
        }
        return quote;
    }

}