package main.java.sports_betting.bet_scraper;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProcessHTML {

    public static void main(String[] args) {
        final int NUM_DAYS_PAST = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_PAST", "7"));
        final int NUM_DAYS_FUTURE = Integer.parseInt(System.getenv().getOrDefault("NUM_DAYS_FUTURE", "7"));
        final LocalDate startDate = LocalDate.now().minusDays(NUM_DAYS_PAST);
        final LocalDate endDate = LocalDate.now().plusDays(NUM_DAYS_FUTURE);
        File bettingDir = new File("betting_data");
        MongoDBHelper.init();
        ExecutorService service = Executors.newFixedThreadPool(4);
        for (File bettingFile : bettingDir.listFiles()) {
            AtomicLong cnt = new AtomicLong(0);
            System.out.println("Counting: " + bettingFile.getName());
            AtomicLong bets = new AtomicLong(0);
            AtomicLong odds = new AtomicLong(0);
            String sport = bettingFile.getName();
            String betType;
            boolean basketballOrFootball = sport.startsWith("ncaa") || sport.startsWith("nba") || sport.startsWith("nfl");
            if (sport.contains("_")) {
                betType = sport.split("_")[1];
                sport = sport.split("_")[0];
                betType = ScrapeSBRHelper.friendlyBetTypeFor(betType + "/", basketballOrFootball);
            } else {
                if (basketballOrFootball) {
                    betType = ScrapeSBRHelper.SPREAD_FRIENDLY;
                } else {
                    betType = ScrapeSBRHelper.MONEY_LINE_FRIENDLY;
                }
            }
            final String _betType = betType;
            final String _sport = sport;
            LocalDate date = startDate;
            while (endDate.isAfter(date)) {
                final LocalDate _date = date;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        File betFile = new File(bettingFile, _date.format(DateTimeFormatter.BASIC_ISO_DATE));
                        if (betFile.exists()) {
                            try {
                                String html = FileUtils.readFileToString(betFile, StandardCharsets.UTF_8);
                                if (html != null) {
                                    Document doc = Jsoup.parse(html);
                                    synchronized (ProcessHTML.class) {
                                        processHTML(_sport, html, _betType, _date);
                                    }
                                    Elements teams = doc.select("#booksData div.event-holder.holder-complete .eventLine-team .team-name");
                                    Elements oddsLinks = doc.select("#booksData div.event-holder.holder-complete .eventLine-book b");
                                    int nOdds = 0;
                                    for (Element oddsLink : oddsLinks) {
                                        if (oddsLink.text().trim().length() > 0) {
                                            nOdds++;
                                        }
                                    }
                                    odds.getAndAdd(nOdds / 2);
                                    int numBets = teams.size() / 2;
                                    bets.getAndAdd(numBets);
                                    cnt.getAndIncrement();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                runnable.run();
                date = date.plusDays(1);
            }
            System.out.println("Total days: " + cnt.get());
            System.out.println("Total events: " + bets.get());
            System.out.println("Total odds: " + odds.get());
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            MongoDBHelper.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void processHTML(String sport, String html, String betType, LocalDate date) throws SQLException {
        Document document = Jsoup.parse(html);
        Elements feedHeader = document.select("#feedHeader .carousel-bookslist ul li a");
        Map<Integer,String> bookIdToBookMap = new HashMap<>();
        for(Element header : feedHeader) {
            bookIdToBookMap.put(Integer.valueOf(header.attr("book")), header.text());
        }
        Elements eventLines = document.select("div.event-holder");
        for(Element element : eventLines) {
            Element eventLine = element.children().get(0);
            //Element eventBox = element.children().get(1);
            //System.out.println(element.html());
            Elements teams = eventLine.select(".eventLine-team .eventLine-value,.eventLine-valueD");
            if(teams.size()<2) {
                //System.out.println("Could not find teams in: "+teams);
                continue;
            }
            String team1 = teams.get(0).text();
            String team2 = teams.get(1).text();
            Integer winner = null;
            if (teams.get(0).select(".icons-winner-arrow").size() > 0) {
                winner = 0;
            } else if (teams.get(1).select(".icons-winner-arrow").size() > 0) {
                winner = 1;
            } else if (teams.size() > 2 && teams.get(2).select(".icons-winner-arrow").size() > 0) {
                winner = 2;
            }
            Elements scoreBoard = eventLine.select(".scorebox .score");
            Map<String, Object> results = new HashMap<>();

            if (scoreBoard.size() > 0) {
                Elements scorePeriods = scoreBoard.select(".score-periods");
                if (scorePeriods.size() > 1) {
                    //System.out.println(scoreBoard.html());
                    List<String> periods1 = scorePeriods.get(0).select(".period").eachText();
                    List<String> periods2 = scorePeriods.get(1).select(".period").eachText();
                    String currentScore1 = scorePeriods.get(0).select(".current-score").text().trim();
                    String currentScore2 = scorePeriods.get(1).select(".current-score").text().trim();
                    String finalScore1 = scorePeriods.get(0).select(".final,.total").text().trim();
                    String finalScore2 = scorePeriods.get(1).select(".final,.total").text().trim();
                    try {
                        List<Integer> nPeriods1 = periods1.stream().map((n)->Integer.parseInt(n, 10)).collect(Collectors.toList());
                        List<Integer> nPeriods2 = periods2.stream().map((n)->Integer.parseInt(n, 10)).collect(Collectors.toList());
                        int final1 = Math.max(Integer.parseInt(finalScore1, 10), Integer.parseInt(currentScore1));
                        int final2 = Math.max(Integer.parseInt(finalScore2, 10), Integer.parseInt(currentScore2));
                        results.put("away_score", final1);
                        results.put("home_score", final2);
                        results.put("away_score_periods", nPeriods1);
                        results.put("home_score_periods", nPeriods2);
                    } catch(Exception e) {
                        // team is likely still in play
                        //System.out.println(scoreBoard.html());
                        //System.out.println("Team1: "+team1);
                        //System.out.println("Team2: "+team2);
                        //System.out.println("Sport: "+sport);
                        //System.out.println("Date: "+date.toString());
                    }

                } else {
                    Elements sets = scoreBoard.select(".sets");
                    if (sets.size() > 0) {
                        List<Integer> sets1 = new ArrayList<>(5);
                        List<Integer> sets2 = new ArrayList<>(5);
                        int setsWon1 = 0;
                        int setsWon2 = 0;
                        try {
                            for (Element set : sets.get(0).children()) {
                                int games1 = Integer.parseInt(set.select("span").get(0).text().trim(), 10);
                                int games2 = Integer.parseInt(set.select("span").get(1).text().trim(), 10);
                                sets1.add(games1);
                                sets2.add(games2);
                                if (games1 > games2) {
                                    setsWon1++;
                                } else if (games2 > games1) {
                                    setsWon2++;
                                }
                            }
                            results.put("away_sets", sets1);
                            results.put("home_sets", sets2);
                            results.put("away_sets_won", setsWon1);
                            results.put("home_sets_won", setsWon2);
                        } catch(Exception e) {
                            e.printStackTrace();;
                            // likely still in progress
                        }
                    }
                }
            }
            //System.out.println(new Gson().toJson(results));

            String timeStr = eventLine.select(".eventLine-time").text().trim();
            //timeStr = timeStr.replace("p", "PM").replace("a", "AM");
            //System.out.println(timeStr);
            LocalTime time = LocalTime.of(Integer.parseInt(timeStr.split(":")[0], 10), Integer.parseInt(timeStr.split(":")[1].replace("p", "").replace("a", "")));
            time = time.minusHours(3);
            if (timeStr.endsWith("p")) {
                time = time.plusHours(12);
            }
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            //System.out.println(dateTime);
            if (betType.equals(ScrapeSBRHelper.MONEY_LINE_FRIENDLY)) {
                ScrapeSBRHelper.ingestGame(
                        sport, date, team1, team2, dateTime, winner, results
                );
            }
            Elements books = eventLine.select(".eventLine-book");
            Elements opener = eventLine.select(".eventLine-opener");
            List<Element> allBooks = new ArrayList<>(books);
            if (opener.size()>0) {
                allBooks.add(0, opener.get(0));
            }
            for(Element book : allBooks) {
                if (sport.equals("nba") && date.equals(LocalDate.of(2022, 1, 30))) {
                    System.out.println("Teams: "+team1+", "+team2);
                    System.out.println("Book: "+book.html());
                }
                String bookName;
                int rel;
                if (book.hasClass("eventLine-opener")) {
                    bookName = "_opener_";
                    rel = 0;
                } else {
                    rel = Integer.valueOf(book.attr("rel"));
                    bookName = bookIdToBookMap.get(rel);
                }
                Elements bookValues = book.children();
                String val1 = bookValues.get(0).text();
                if(val1 == null || val1.trim().isEmpty()) {
                    continue;
                }
                String val2 = bookValues.get(1).text();
                if(val2==null || val2.trim().isEmpty()) {
                    continue;
                }
                val1 = val1.replaceAll("&"+"nbsp;", " ").replaceAll(String.valueOf((char) 160), " ");
                val2 = val2.replaceAll("&"+"nbsp;", " ").replaceAll(String.valueOf((char) 160), " ");
                val1 = handleFractions(val1);
                val2 = handleFractions(val2);
                String p1;
                String p2;
                if(betType.equals(ScrapeSBRHelper.TOTALS_FRIENDLY)) {
                    String[] val1Split = val1.split(" ");
                    if(val1Split.length<2) {
                        //System.out.println("Illegal val1: "+val1);
                        //System.out.println("Book: "+book.toString());
                        continue;
                    }
                    String[] val2Split = val2.split(" ");
                    if(val2Split.length<2) {
                        //System.out.println("Illegal val2: "+val2);
                        //System.out.println("Book: "+book.toString());
                        continue;
                    }
                    p1 = val1Split[1];
                    p2 = val2Split[1];
                    val1 = val1Split[0];
                    val2 = val2Split[0];
                    BigDecimal total1 = new BigDecimal(val1);
                    BigDecimal total2 = new BigDecimal(val2);
                    BigDecimal price1 = new BigDecimal(p1);
                    BigDecimal price2 = new BigDecimal(p2);
                    ScrapeSBRHelper.ingestTotals(sport, date, bookName, rel, team1, team2, total1, total2, price1, price2);

                } else if (betType.equals(ScrapeSBRHelper.SPREAD_FRIENDLY)) {
                    String[] val1Split = val1.split(" ");
                    if(val1Split.length<2) {
                        System.out.println("Illegal val1: "+val1);
                        System.out.println("Book: "+book.toString());
                        continue;
                    }
                    String[] val2Split = val2.split(" ");
                    if(val2Split.length<2) {
                        System.out.println("Illegal val2: "+val2);
                        System.out.println("Book: "+book.toString());
                        continue;
                    }
                    p1 = val1Split[1];
                    p2 = val2Split[1];
                    val1 = val1Split[0];
                    val2 = val2Split[0];
                    try {
                        BigDecimal spread1 = new BigDecimal(val1);
                        BigDecimal spread2 = new BigDecimal(val2);
                        BigDecimal price1 = new BigDecimal(p1);
                        BigDecimal price2 = new BigDecimal(p2);
                        ScrapeSBRHelper.ingestSpread(sport, date, bookName, rel, team1, team2, spread1, spread2, price1, price2);
                    } catch(Exception e) {
                        System.out.println("Number exception: ");
                        System.out.println(val1+" "+val2+" "+p1+" "+p2);
                    }
                } else {
                    BigDecimal price1 = new BigDecimal(val1);
                    BigDecimal price2 = new BigDecimal(val2);
                    ScrapeSBRHelper.ingestMoneyLine(sport, date, bookName, rel, team1, team2, price1, price2, null);
                }
            }
        }
    }

    private static String handleFractions(String in) {
        return in.replace("PK", "0 ").replace("  "," ")
                .replace("⅘",".8").replace("⅓", ".33").replace("¼",".25").replace("¾",".75").replace("½", ".5").trim();
    }
}
