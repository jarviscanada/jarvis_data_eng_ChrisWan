package ca.jrvs.apps.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;


public class QuoteHttpHelperTest {

    @Test
    void testFetchQuoteInfo() {
        OkHttpClient client = new OkHttpClient();
        String apiKey = "f46e6402bamshb3fa3b36bf72984p1ffd83jsnbb4801711afe";
        String symbol = "MSFT";
        QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(apiKey, client);
        Quote quote = quoteHttpHelper.fetchQuoteInfo(symbol);
        assertEquals("MSFT", quote.getTicker());
    }
}
