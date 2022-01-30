package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeMLS {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-mls",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerMLSUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
