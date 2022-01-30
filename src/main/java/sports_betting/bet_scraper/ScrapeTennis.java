package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeTennis {
    public static void run(boolean useCache, LocalDate date) throws Exception {
        ScrapeBetsBySport.ingest(
                "tennis",
                ScrapeSBRHelper.TENNIS_BET_TYPES,
                pair-> ScrapeSBRHelper.tennisUrlFor(pair.getKey(),pair.getValue()),
                useCache,
                date
        );
    }

}
