import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {
    public static long getCurrentDateTime() {
        Date date = new Date();
        long timeMilli = date.getTime();
        return timeMilli;
    }
}
