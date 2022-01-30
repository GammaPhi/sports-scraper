package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeChampionsLeague {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "soccer-champions-league",
                ScrapeSBRHelper.SOCCER_BET_TYPES,
                pair-> ScrapeSBRHelper.soccerChampionsLeagueUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
