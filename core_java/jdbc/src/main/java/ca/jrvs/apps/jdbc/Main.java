package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.controller.StockQuoteController;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.service.PositionService;
import ca.jrvs.apps.jdbc.service.QuoteService;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    static final Logger infoLogger = LoggerFactory.getLogger("infoLogger");
    static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    public static void main(String[] args) {
        infoLogger.info("Started main application");
        Map<String, String> properties = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/properties.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                properties.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException e) {
            errorLogger.error("Unable to find file {}", e.getMessage());
        } catch (IOException e) {
            errorLogger.error("IO Exception on {}", e.getMessage());
        }

        try {
            Class.forName(properties.get("db-class"));
        } catch (ClassNotFoundException e) {
            errorLogger.error("Unable to find db-class on properties.txt {}", e.getMessage());
        }
        OkHttpClient client = new OkHttpClient();
        String url = "jdbc:postgresql://"+properties.get("server")+":"+properties.get("port")+"/"+properties.get("database");
        try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
            QuoteDao qRepo = new QuoteDao(c);
            PositionDao pRepo = new PositionDao(c);
            QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"), client);
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo, sQuote);
            StockQuoteController con = new StockQuoteController(sQuote, sPos);
            con.initClient();
            infoLogger.info("Successfully started the application");
        } catch (SQLException e) {
            errorLogger.error("SQL Exception on {}", e.getMessage());
        }
    }
}