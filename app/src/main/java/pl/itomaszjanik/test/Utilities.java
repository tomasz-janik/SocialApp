package pl.itomaszjanik.test;

import android.content.Context;
import android.widget.Toast;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utilities {

    public static boolean checkComment(String string, Context context){
        if (string == null || string.equals("")){
            Toast.makeText(context, context.getResources().getString(R.string.comment_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (string.length() > Values.COMMENT_LIMIT){
            Toast.makeText(context, context.getResources().getString(R.string.comment_too_long), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String decodeDate(String date, Context context){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime creationTime = dateTimeFormatter.parseDateTime(date);

        Interval interval = new Interval(creationTime, new Instant());
        Duration duration = interval.toDuration();
        String output = "";
        if (duration.getStandardDays() >= 356){
            long years = duration.getStandardDays()/356;
            if (years == 1){
                output += context.getResources().getString(R.string.year_one);
            }
            else if (years < 5){
                output += years + " ";
                output += context.getResources().getString(R.string.year_two);
            }
            else{
                output += years + " ";
                output += context.getResources().getString(R.string.year_five);
            }
        }
        else if (duration.getStandardDays() >= 31){
            long months = duration.getStandardDays() / 31;
            if (months == 1){
                output += context.getResources().getString(R.string.month_one);
            }
            else if (months < 5){
                output += months + " ";
                output += context.getResources().getString(R.string.month_two);
            }
            else{
                output += months + " ";
                output += context.getResources().getString(R.string.month_five);
            }
        }
        else if (duration.getStandardDays() >= 7){
            long weeks = duration.getStandardDays() / 7;
            if (weeks == 1){
                output += context.getResources().getString(R.string.week_one);
            }
            else if (weeks < 5){
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_two);
            }
            else{
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_five);
            }
        }
        else if (duration.getStandardDays() >= 1){
            long days = duration.getStandardDays();
            if (days == 1){
                output += context.getResources().getString(R.string.day_one);
            }
            else{
                output += days + " ";
                output += context.getResources().getString(R.string.week_two);
            }
        }
        else if (duration.getStandardHours() >= 1){
            long hours = duration.getStandardHours();
            if (hours == 1){
                output += context.getResources().getString(R.string.hour_one);
            }
            else if (hours < 5){
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_two);
            }
            else{
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_five);
            }
        }
        else if (duration.getStandardMinutes() >= 1){
            long minutes = duration.getStandardMinutes();
            if (minutes == 1){
                output += context.getResources().getString(R.string.minute_one);
            }
            else if (minutes < 5){
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_two);
            }
            else{
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_five);
            }
        }
        else{
            output += context.getResources().getString(R.string.second);
        }
        return (output + " temu");
    }


}
