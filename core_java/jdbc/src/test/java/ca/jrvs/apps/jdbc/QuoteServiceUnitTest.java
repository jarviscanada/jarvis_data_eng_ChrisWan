package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.service.QuoteService;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceUnitTest {
    @Mock
    private QuoteDao mockQuoteDao;
    @Mock
    private QuoteHttpHelper mockQuoteHttpHelper;
    @InjectMocks
    private QuoteService quoteService;

    @BeforeEach
    void setup() {
        quoteService = new QuoteService(mockQuoteDao, mockQuoteHttpHelper);
    }

    @Test
    void fetchQuoteDataFromAPI_Pass() {
        String ticker = "MSFT";
        Quote mockQuote = new Quote();
        mockQuote.setTicker(ticker);
        mockQuote.setOpen(150.74);
        mockQuote.setHigh(180.91);
        mockQuote.setLow(100.59);
        mockQuote.setPrice(160.74);
        mockQuote.setVolume(9018490);
        mockQuote.setLatestTradingDay(new Date(2024, 8, 10));
        mockQuote.setPreviousClose(154.90);
        mockQuote.setChange(4.90);
        mockQuote.setChangePercent("3%");
        mockQuote.setTimestamp(new Timestamp(System.currentTimeMillis()));

        when(mockQuoteHttpHelper.fetchQuoteInfo(ticker)).thenReturn(mockQuote);
        when(mockQuoteDao.save(mockQuote)).thenReturn(mockQuote);

        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);
        assertTrue(result.isPresent());
        assertEquals(mockQuote, result.get());
        verify(mockQuoteHttpHelper).fetchQuoteInfo(ticker);
        verify(mockQuoteDao).findById(ticker);
        verify(mockQuoteDao).save(mockQuote);
    }

}
