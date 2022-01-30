package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeNCAAB {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "ncaab",
                ScrapeSBRHelper.NCAAB_BET_TYPES,
                pair-> ScrapeSBRHelper.ncaabUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
