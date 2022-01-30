package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeBundesliga {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-bundesliga",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerGERUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
