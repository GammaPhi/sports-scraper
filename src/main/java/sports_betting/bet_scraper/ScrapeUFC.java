package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeUFC {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "ufc",
                ScrapeSBRHelper.UFC_BET_TYPES,
                pair-> ScrapeSBRHelper.ufcUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
