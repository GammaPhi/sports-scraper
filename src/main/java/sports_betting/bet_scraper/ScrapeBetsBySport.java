package main.java.sports_betting.bet_scraper;


import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ScrapeBetsBySport {
    public static void ingest(String sportName, Collection<String> betTypes, Function<Pair<String,LocalDate>, String> urlFunction, boolean useCache) throws Exception {
        ingest(sportName, betTypes, urlFunction, useCache, null);
    }
    public static void ingest(String sportName, Collection<String> betTypes, Function<Pair<String,LocalDate>, String> urlFunction, boolean useCache, LocalDate _) throws Exception {
        final int NUM_DAYS_PAST = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_PAST", "7"));
        final int NUM_DAYS_FUTURE = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_FUTURE", "7"));
        final LocalDate startDate = LocalDate.now().minusDays(NUM_DAYS_PAST);
        final LocalDate endDate = LocalDate.now().plusDays(NUM_DAYS_FUTURE);
        LocalDate date = startDate;
        ExecutorService service = Executors.newFixedThreadPool(10);
        while(endDate.isAfter(date)) {
            final LocalDate _date = date;
            service.execute(() -> {
                for (String bet_type : betTypes) {
                    String url = urlFunction.apply(new Pair<>(bet_type,_date));
                    if(bet_type.length()>0) {
                        bet_type="_"+bet_type;
                    }
                    File sportDir = new File(new File("betting_data"), sportName+bet_type);
                    String html = ScrapeSBRHelper.getHTML(sportDir, url, useCache, true, true);

                    if (html == null) break; // won't be other fields

                }
            });
            date = date.plusDays(1);
        }
        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
}
