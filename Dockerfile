FROM openjdk:8

WORKDIR /opt/

COPY target/sports-scraper-1.0-SNAPSHOT-jar-with-dependencies.jar sports-scraper.jar

ENTRYPOINT ["java", "-jar", "sports-scraper.jar"]