package main.java.sports_betting.bet_scraper;

import java.util.concurrent.TimeUnit;

public class Launcher {
    public static void main(String[] args) {
        final int INTERVAL_SECONDS  = Integer.parseInt(System.getenv().getOrDefault("INTERVAL_SECONDS", "300"), 10);
        while (true) {
            try {
                ScrapeAll.main(args);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error during scraping...");
            }
            try {
                ProcessHTML.main(args);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error during processing...");
            }
            try {
                System.out.println("Sleeping for "+INTERVAL_SECONDS+" seconds...");
                TimeUnit.SECONDS.sleep(INTERVAL_SECONDS);
            } catch(InterruptedException ioe) {
                System.out.println("Interrupted.");
                break;
            }
        }
    }
}
