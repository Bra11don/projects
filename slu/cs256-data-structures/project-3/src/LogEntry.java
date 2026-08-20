public class LogEntry {

    private String raw;
    private int entryId;
    private String category;
    private long timestamp;

    /**
     * breaking down the components of each log entri
     * @param line
     */
    public LogEntry(String line, int count){
        raw = line;

        int i = 15;
        while(true){
            if (line.charAt(i)=='|'){
                break;
            }
            i++;
        }
        category = line.substring(15,i).toLowerCase();
        timestamp = convertTimetoLong(line);
        entryId = count;
    }

    public LogEntry(long value) {
        timestamp = value;
    }

    public int getId() {
        return entryId;
    }

    public static long convertTimetoLong(String time){
        //M M : D D : h h : m m  :  s  s
        //0 1 2 3 4 5 6 7 8 9 10 11 12 13
        return (time.charAt(13) - '0') * 1L + (time.charAt(12)-'0')*10L + (time.charAt(10)-'0')*100L + (time.charAt(9)-'0')*1000L + (time.charAt(7)-'0')*10000L+(time.charAt(6)-'0')*100000L+(time.charAt(4)-'0')*1000000L+(time.charAt(3)-'0')*10000000L+(time.charAt(1)-'0')*100000000L+(time.charAt(0)-'0')*1000000000L;

        //ALTERNATIVELY
        //return Long.parseLong(time.substring(0,14).replace(":","")); //this is apparently faster but the problem with
        // this is that
        // its doing extra work iterating through the string and replacing which in some cases mayyy be slow but overall its not dramatically slow

    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return raw;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
