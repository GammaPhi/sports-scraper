package main.java.sports_betting.bet_scraper;

import com.mongodb.BasicDBObject;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class ScrapeSBRHelper {
    public static final String MONEY_LINE_FRIENDLY = "money_line";
    public static final String TOTALS_FRIENDLY = "totals";
    public static final String SPREAD_FRIENDLY = "spread";
    public static final String MONEY_LINE_TYPE = "money-line/";
    public static final String MONEY_LINE_DEFAULT_TYPE = "";
    public static final String SPREAD_DEFAULT_TYPE = "";
    public static final String TOTALS_TYPE = "totals/";
    public static final String POINT_SPREAD_TYPE = "pointspread/";
    public static final Collection<String> NBA_BET_TYPES = Arrays.asList(
            MONEY_LINE_TYPE,
            SPREAD_DEFAULT_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> NCAAB_BET_TYPES = Arrays.asList(
            MONEY_LINE_TYPE,
            SPREAD_DEFAULT_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> MLB_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            POINT_SPREAD_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> SOCCER_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            POINT_SPREAD_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> TENNIS_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            POINT_SPREAD_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> UFC_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> BOXING_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> NHL_BET_TYPES = Arrays.asList(
            MONEY_LINE_DEFAULT_TYPE,
            POINT_SPREAD_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> NFL_BET_TYPES = Arrays.asList(
            MONEY_LINE_TYPE,
            SPREAD_DEFAULT_TYPE,
            TOTALS_TYPE
    );
    public static final Collection<String> NCAAF_BET_TYPES = Arrays.asList(
            MONEY_LINE_TYPE,
            SPREAD_DEFAULT_TYPE,
            TOTALS_TYPE
    );

        /*
    private static Connection conn;
    static {
        try {
            Map<String, String> env = System.getenv();
            String host = env.getOrDefault("DB_HOST", "localhost");
            String port = env.getOrDefault("DB_PORT", "5432");
            String user = env.getOrDefault("DB_USER", "postgres");
            String pass = env.getOrDefault("DB_PASS", "");
            String name = env.getOrDefault("DB_NAME", "sports_betting");
            conn = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+name+"?user="+user+"&password="+pass+"&tcpKeepAlive=true");
        } catch (Exception e) {
            conn = null;
            e.printStackTrace();
            System.out.println("Unable to connect to database.");
        }
        if(conn!=null) {
            try {
                conn.setAutoCommit(false);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    public static String friendlyBetTypeFor(String bet_type, boolean spreadDefault) {
        if(bet_type.equals(MONEY_LINE_TYPE)||(!spreadDefault && bet_type.equals(MONEY_LINE_DEFAULT_TYPE))) {
            return MONEY_LINE_FRIENDLY;
        } else if(bet_type.equals(TOTALS_TYPE)) {
            return TOTALS_FRIENDLY;
        } else if((spreadDefault&&bet_type.equals(SPREAD_DEFAULT_TYPE)) || bet_type.equals(POINT_SPREAD_TYPE)) {
            return SPREAD_FRIENDLY;
        } else {
            throw new RuntimeException("Unknown bet_type: "+bet_type);
        }
    }
    static String nbaUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/nba-basketball/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String ncaabUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/ncaa-basketball/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String ncaafUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/college-football/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String mlbUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/mlb-baseball/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String nhlUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/nhl-hockey/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String soccerUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerITAUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=serie-a&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerBRAUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=brazil-serie-a&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerFRAUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=ligue1&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String soccerGERUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=bundesliga&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerENGUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=english-premier-league&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerWorldCupUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=world-cup&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerMLSUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=mls&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerChampionsLeagueUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=champions-league&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    static String soccerMEXUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/soccer/"+type+"?leagueId=liga-mexicana&date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String tennisUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/tennis/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String ufcUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/ufc/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String boxingUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/boxing/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    static String nflUrlFor(String type, LocalDate date) {
        return "https://classic.sportsbookreview.com/betting-odds/nfl-football/"+type+"?date="+date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String getHTML(File rootDir, String endpoint, boolean useCache, boolean download, boolean isSBR) {
        return getHTML(rootDir, endpoint,useCache,download,isSBR,null);
    }

    public static String getHTML(File rootDir, String endpoint, boolean useCache, boolean download, boolean isSBR, String useSelenium) {
        try {
            endpoint = endpoint.replace(" ","%20");
            String fileName = endpoint.split("(\\?|\\&)date=",2)[1];
            File file = new File(rootDir, fileName);
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }
            System.out.println("Saving to: "+ file.getAbsolutePath());
            String json;
            if(file.exists() && useCache) {
                json = FileUtils.readFileToString(file, Charset.defaultCharset());
            } else {
                if (!download) {
                    return null;
                } else {
                    URL url = new URL(endpoint);
                    System.out.println("Seeking: " + url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Cache-Control", "max-age=0");
                    conn.setRequestProperty("Accept-Language", "he-IL,he;q=0.8,en-US;q=0.6,en;q=0.4");
                    conn.setRequestProperty("Host", "stats.nba.com");
                    // conn.setRequestProperty("Referrer", "http://stats.nba.com/");
                    //conn.setRequestProperty("Cookie", "ugs=1; ug=5afda6da07bd690a3c25f63b560070ae; check=true; s_cc=true; s_sq=%5B%5BB%5D%5D; ak_bmsc=E02D450754096F0C57DA6238F2FE19DAA5FE01AB6F270000C9DFFD5AD6623213~pl4X1WqD+EcfKLrK+AqrtcIdfabUDkW+MzVHTs0ql5r/OcVyVkhvJu3XC2zusZFzWpbgwRpyRNdK/ZuEaV4OuiV9YDX7fiJBCYHoTNSJZcYfXX8H7neF4Gjc1OJeFo3igYapPUjc9Wx/1GZa2y3HPsGNTFZyTaf0KKufGLsOqxqLCwSV0ummnfAXci1+Kz9g23SXoXMn25es1ev1Gg21TxQ+m8+ruycbuuyYBFhWi6oqo=; __gads=ID=97349dadae0dc294:T=1526572765:S=ALNI_MaWciNikv6Q9EmEe3gNMS-wEYBLtg; mbox=PC#6e9e4ccf3fd442fe9bb9264762e0d024.28_73#1589817562|session#bcbd2e503d7d419887aa4a9ff3c4a231#1526581578; _gid=GA1.2.917206857.1526572760; s_vi=[CS]v1|2D7ED36D85032E6B-4000119C8018048A[CE]; s_fid=1BACA18AF186F094-30F096F27E12490A; _ga=GA1.2.629689711.1526572760");
                    conn.setRequestProperty("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();
                    json = result.toString();
                }
                if(isSBR) {
                    org.jsoup.nodes.Document doc = Jsoup.parse(json);
                    Elements elements = doc.select("#bettingOddsGridContainer");
                    json = elements.toString();
                    if (json.trim().length() == 0) {
                        return null;
                    }
                    if (json.contains("No odds available at this time for this league")) {
                        System.out.println("No odds found...");
                        return null;
                    }
                }
                boolean finished = false;
                try {
                    FileUtils.writeStringToFile(file, json, "UTF-8");
                    finished = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    if (!finished) {
                        file.delete();
                    }
                }
            }
            // System.out.println(json);
            return json;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ingestGame(String sport, LocalDate date, String team1, String team2, LocalDateTime dateTime, Integer winner, Map<String, Object> results) throws SQLException {
        ZoneOffset offset = ZoneId.of("America/Los_Angeles").getRules().getOffset(dateTime);
        long epoch = dateTime.toEpochSecond(offset);

        Map<String, Object> data = new HashMap<>();
        data.put("sport", sport);
        data.put("date", date);
        data.put("away_team", team1);
        data.put("home_team", team2);
        data.put("timestamp", epoch);
        data.put("results", new BasicDBObject(results));
        data.put("winner_index", winner);
        MongoDBHelper.upsert(
                "sports",
                "games",
                data,
                Arrays.asList("sport", "date", "away_team", "home_team")
        );
    }


    public static void ingestTotals(String sport, LocalDate date, String bookName, Integer rel, String team1, String team2, BigDecimal total1, BigDecimal total2, BigDecimal price1, BigDecimal price2) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("sport", sport);
        data.put("date", date);
        data.put("book_id", rel);
        data.put("book_name", bookName);
        data.put("away_total", total1);
        data.put("home_total", total2);
        data.put("away_price", price1);
        data.put("home_price", price2);
        data.put("away_team", team1);
        data.put("home_team", team2);
        MongoDBHelper.upsert(
                "sports",
                "totals",
                data,
                Arrays.asList("sport", "date", "book_id", "away_team", "home_team")
        );
    }

    public static void ingestSpread(String sport, LocalDate date, String bookName, Integer rel, String team1, String team2, BigDecimal spread1, BigDecimal spread2, BigDecimal price1, BigDecimal price2) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        data.put("sport", sport);
        data.put("date", date);
        data.put("book_id", rel);
        data.put("book_name", bookName);
        data.put("away_spread", spread1);
        data.put("home_spread", spread2);
        data.put("away_price", price1);
        data.put("home_price", price2);
        data.put("away_team", team1);
        data.put("home_team", team2);
        MongoDBHelper.upsert(
                "sports",
                "spreads",
                data,
                Arrays.asList("sport", "date", "book_id", "away_team", "home_team")
        );
    }

    public static void ingestMoneyLine(String sport, LocalDate date, String bookName, Integer rel, String team1, String team2, BigDecimal price1, BigDecimal price2, String oddsPortalTournament) throws SQLException {
        /*PreparedStatement ps = conn.prepareStatement("insert into money_lines " +
                "(sport,date,book_name,book_id,team1,team2,price1,price2"+(oddsPortalTournament==null?") ":",odds_portal_tournament) ") +
                "values (?,?,?,?,?,?,?,?"+(oddsPortalTournament==null?")":",?)")+" on conflict (sport,date,book_id,team1,team2) do update set (book_name,price1,price2"+(oddsPortalTournament==null?")":",odds_portal_tournament)")+"= (excluded.book_name,excluded.price1,excluded.price2"+(oddsPortalTournament==null?")":", excluded.odds_portal_tournament)"));
        ps.setObject(1, sport);
        ps.setObject(2, Date.valueOf(date));
        ps.setObject(3, bookName);
        ps.setObject(4, rel);
        ps.setObject(5, team1);
        ps.setObject(6, team2);
        ps.setObject(7, price1);
        ps.setObject(8, price2);
        if(oddsPortalTournament!=null) {
            ps.setObject(9, oddsPortalTournament);
        }
        ps.executeUpdate();*/
        Map<String, Object> data = new HashMap<>();
        data.put("sport", sport);
        data.put("date", date);
        data.put("book_id", rel);
        data.put("book_name", bookName);
        data.put("away_price", price1);
        data.put("home_price", price2);
        data.put("away_team", team1);
        data.put("home_team", team2);
        MongoDBHelper.upsert(
                "sports",
                "moneylines",
                data,
                Arrays.asList("sport", "date", "book_id", "away_team", "home_team")
        );
    }
}
