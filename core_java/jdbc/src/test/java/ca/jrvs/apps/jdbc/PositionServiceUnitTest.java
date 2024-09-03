package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.service.PositionService;
import ca.jrvs.apps.jdbc.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class PositionService_UnitTest {
    @Mock
    private PositionDao mockPositionDao;
    @Mock
    private QuoteService mockQuoteService;
    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setup() {
        positionService = new PositionService(mockPositionDao, mockQuoteService);
    }

    @Test
    void buy_Pass() {
        String ticker = "MSFT";
        Quote mockQuote = new Quote();
        double price = 180.0;
        int numberOfShares = 1000;
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
        Position mockPosition = new Position();
        mockPosition.setTicker(ticker);
        mockPosition.setNumOfShares(numberOfShares);
        mockPosition.setValuePaid(numberOfShares * price);

        when(mockQuoteService.fetchQuoteDataFromAPI(ticker)).thenReturn(Optional.of(mockQuote));
        when(mockPositionDao.findById(ticker)).thenReturn(Optional.empty());
        when(mockPositionDao.save(any(Position.class))).thenReturn(mockPosition);

        Position result = positionService.buy(ticker, numberOfShares, numberOfShares * price);
        assertNotNull(result);
        assertEquals(mockPosition, result);
        verify(mockQuoteService).fetchQuoteDataFromAPI(ticker);
        verify(mockPositionDao).findById(ticker);
    }

    @Test
    void sell_Pass() {
        String ticker = "MSFT";
        int numberOfShares = 1000;
        double price = 180.0;
        Position mockPosition = new Position();
        mockPosition.setTicker(ticker);
        mockPosition.setNumOfShares(numberOfShares);
        mockPosition.setValuePaid(numberOfShares * price);

        when(mockPositionDao.findById(ticker)).thenReturn(Optional.of(mockPosition));
        doNothing().when(mockPositionDao).deleteById(ticker);

        positionService.sell(ticker);

        verify(mockPositionDao).findById(ticker);
        verify(mockPositionDao).deleteById(ticker);
    }
}
