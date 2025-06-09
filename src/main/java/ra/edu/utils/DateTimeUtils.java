package ra.edu.utils;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class DateTimeUtils {
    public String getTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "unknown time";
        LocalDateTime now = LocalDateTime.now();

        boolean isFuture = createdAt.isAfter(now);

        LocalDateTime from = isFuture ? now : createdAt;
        LocalDateTime to = isFuture ? createdAt : now;

        long years = from.until(to, ChronoUnit.YEARS);
        from = from.plusYears(years);

        long months = from.until(to, ChronoUnit.MONTHS);
        from = from.plusMonths(months);

        long days = from.until(to, ChronoUnit.DAYS);
        from = from.plusDays(days);

        long hours = from.until(to, ChronoUnit.HOURS);
        from = from.plusHours(hours);

        long minutes = from.until(to, ChronoUnit.MINUTES);
        from = from.plusMinutes(minutes);

        long seconds = from.until(to, ChronoUnit.SECONDS);

        String result;
        if (years > 0) result = years + " year" + (years > 1 ? "s" : "");
        else if (months > 0) result = months + " month" + (months > 1 ? "s" : "");
        else if (days > 0) result = days + " day" + (days > 1 ? "s" : "");
        else if (hours > 0) result = hours + " hour" + (hours > 1 ? "s" : "");
        else if (minutes > 0) result = minutes + " minute" + (minutes > 1 ? "s" : "");
        else result = "just now";

        if (!result.equals("just now")) {
            result = isFuture ? "in " + result : result + " ago";
        }

        return result;
    }

}
