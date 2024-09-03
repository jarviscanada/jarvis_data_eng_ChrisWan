package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dto.Quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {

    private String apiKey;
    private OkHttpClient client;
    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    public QuoteHttpHelper(String apiKey, OkHttpClient client) {
        this.apiKey = apiKey;
        this.client = client;
    }

    /**
     * Fetch latest quote data from Alpha Vantage endpoint
     * @param symbol
     * @return Quote with latest data
     * @throws IllegalArgumentException - if no data was found for the given symbol
     */
    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {

        infoLogger.info("Started fetchQuoteInfo");
        Quote quote = new Quote();
        Dotenv dotenv = Dotenv.configure().load();

        String apiKey = dotenv.get("API_KEY");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json"))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode globalQuoteNode = jsonNode.get("Global Quote");
            if (globalQuoteNode == null || globalQuoteNode.isEmpty()) {
                throw new IllegalArgumentException(
                        "No data for the symbol was found. Make sure the symbol is valid");
            }
            quote = objectMapper.convertValue(globalQuoteNode, Quote.class);
            quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
            return quote;

        } catch (InterruptedException e) {
            errorLogger.error("Interrupted Exception on {}", e.getMessage());
        } catch (JsonMappingException e) {
            errorLogger.error("Json Mapping Exception on {}", e.getMessage());
        } catch (JsonProcessingException e) {
            errorLogger.error("Json Processing Exception on {}", e.getMessage());
        } catch (IOException e) {
            errorLogger.error("IO Exception on {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            errorLogger.error("Illegal Argument Exception on {}", e.getMessage());
        }
        return quote;
    }
}
