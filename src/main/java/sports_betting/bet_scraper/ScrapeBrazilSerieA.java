package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeBrazilSerieA {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-brazil-serie-a",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerBRAUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
