package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeNFL {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "nfl",
                ScrapeSBRHelper.NFL_BET_TYPES,
                pair-> ScrapeSBRHelper.nflUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
