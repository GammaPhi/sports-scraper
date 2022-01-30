package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeMLB {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "mlb",
                ScrapeSBRHelper.MLB_BET_TYPES,
                pair-> ScrapeSBRHelper.mlbUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
