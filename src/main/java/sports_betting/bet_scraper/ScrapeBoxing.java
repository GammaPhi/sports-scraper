package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeBoxing {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "boxing",
                ScrapeSBRHelper.BOXING_BET_TYPES,
                pair-> ScrapeSBRHelper.boxingUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }
}
