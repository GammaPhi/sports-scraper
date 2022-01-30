package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeNCAAF {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "ncaaf",
                ScrapeSBRHelper.NCAAF_BET_TYPES,
                pair-> ScrapeSBRHelper.ncaafUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
