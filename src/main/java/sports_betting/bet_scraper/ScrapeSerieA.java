package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeSerieA {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-serie-a",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerITAUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
