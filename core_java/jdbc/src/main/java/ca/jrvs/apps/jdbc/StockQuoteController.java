package ca.jrvs.apps.jdbc;

import java.util.Optional;
import java.util.Scanner;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService){
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    /**
     * User interface for our application
     */
    public void initClient() {

        Scanner scanner = new Scanner(System.in);
        int userInput;

        do {
            displayOptions();
            userInput = scanner.nextInt();

            switch (userInput) {
                case 0:
                    System.out.println("Thank you for using our application. Hope to see you soon!");
                    System.out.println("Exiting....");
                    scanner.close();
                    System.out.println("You have successfully exited the program");
                    return;
                case 1:
                    this.viewStockQuote(scanner);
                    break;
                case 2:
                    this.viewAllPositions();
                    break;
                case 3:
                    this.sellStock(scanner);
                    break;
                case 4:
                    this.buyStock(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 4.");
            }

        } while (userInput != 0);
    }

    private void buyStock(Scanner scanner) {
        System.out.println("Enter stock symbol you want to buy: ");
        String symbol = scanner.next().toUpperCase();
        try {
            Optional<Quote> quoteOptional = this.quoteService.fetchQuoteDataFromAPI(symbol);
            if (quoteOptional.isPresent()) {
                double price = quoteOptional.get().getPrice();
                System.out.println("The current price for one share of " + symbol + " is " + price);
                System.out.println("How many shares would you like to purchase? ");
                int purchaseAmount = scanner.nextInt();
                System.out.println(
                        "Your order for " + purchaseAmount + " shares of " + symbol + " costs " + price * purchaseAmount);
                this.positionService.buy(symbol, purchaseAmount, price);
                System.out.println("You have successfully made a purchase");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("You do not have any positions on stock " + symbol);
        }
    }

    private void sellStock(Scanner scanner) {
        System.out.println("Enter stock symbol you want to sell: ");
        String symbol = scanner.next().toUpperCase();

        try {
            this.positionService.sell(symbol);
            System.out.println("You have sucessfully sell all of your shares of: " + symbol);
        } catch (IllegalArgumentException e) {
            System.out.println("You do not have any positions on stock " + symbol);
        }
    }

    private void viewStockQuote(Scanner scanner) {
        System.out.println("Please enter the stock symbol you wish to view: ");
        String symbol = scanner.next().toUpperCase();
        try {
            Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(symbol);
            if (quote.isPresent()) {
                System.out.println("Here is the provided quote");
                System.out.println(quote.toString());
            }
        } catch (IllegalArgumentException e){
            System.out.println("The provided stock symbol does not exist");
        }
    }

    private void viewAllPositions() {
        Iterable<Position> positions = this.positionService.displayAllRecords();

        positions.forEach((element) -> {
            System.out.println("Stock " + element.getTicker());
            System.out.println("Number of Shares:  " + element.getNumOfShares());
            System.out.println("Value Paid:  " + element.getValuePaid());
        });
    }

    private void displayOptions() {
        System.out.println("Welcome to the Stock Quote App, here are the following options you can choose from");
        System.out.println("Press 1 to see a stock quote");
        System.out.println("Press 2 to see all positions");
        System.out.println("Press 3 to sell a stock");
        System.out.println("Press 4 to buy a stock");
        System.out.println("Press 0 to exit this application");
    }

}