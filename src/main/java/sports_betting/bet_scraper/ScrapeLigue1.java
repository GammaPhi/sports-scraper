package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeLigue1 {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-ligue1",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerFRAUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
