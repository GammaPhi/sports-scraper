package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeEPL {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-epl",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerENGUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
