package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeWorldCup {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-world-cup",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerWorldCupUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
