package main.java.sports_betting.bet_scraper;

import java.time.LocalDate;

public class ScrapeAll {
    public static void main(String[] args) {
        final int NUM_DAYS_PAST = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_PAST", "7"));
        final int NUM_DAYS_FUTURE = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_FUTURE", "7"));
        LocalDate date = LocalDate.now().minusDays(NUM_DAYS_PAST);
        boolean useCache = System.getenv().getOrDefault("USE_CACHE", "t").toLowerCase().startsWith("t");

        try {
            ScrapeNBA.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on nba...");
            System.exit(1);
        }

        try {
            ScrapeBrazilSerieA.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeEPL.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeWorldCup.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeChampionsLeague.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeLigaMX.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeBundesliga.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeLigue1.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeSerieA.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ScrapeNCAAF.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on ncaaf...");
            System.exit(1);
        }

        try {
            ScrapeNFL.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on nfl...");
            System.exit(1);
        }


        try {
            ScrapeNHL.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on nhl...");
            System.exit(1);
        }

        try {
            ScrapeMLB.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on mlb...");
            System.exit(1);
        }

        try {
            ScrapeNCAAB.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on ncaab...");
            System.exit(1);
        }

        try {
            ScrapeTennis.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on tennis...");
            System.exit(1);
        }

        try {
            ScrapeBoxing.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on boxing...");
            System.exit(1);
        }

        try {
            ScrapeMLS.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on soccer...");
            System.exit(1);
        }

        try {
            ScrapeUFC.run(useCache, date);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error on ufc...");
            System.exit(1);
        }
    }
}
