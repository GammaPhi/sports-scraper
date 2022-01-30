package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeNBA {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "nba",
                ScrapeSBRHelper.NBA_BET_TYPES,
                pair-> ScrapeSBRHelper.nbaUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
