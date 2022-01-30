package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeNHL {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "nhl",
                ScrapeSBRHelper.NHL_BET_TYPES,
                pair-> ScrapeSBRHelper.nhlUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
