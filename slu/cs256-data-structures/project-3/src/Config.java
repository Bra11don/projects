import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Config {

    private boolean timestampsearch = false;
    private boolean matchingsearch = false;
    private boolean categorysearch = false;
    private boolean keywordsearch = false;
    private boolean appendlogentry = false;
    private boolean appendsearchresults = false;
    private boolean deletelogentry = false;
    private boolean movetobeginning = false;
    private boolean movetoend = false;
    private boolean sortexcerptlist = false;
    private boolean clearexcerptlist = false;
    private boolean pmrsearchresults = false;
    private boolean pexcerptlist = false;
    public int numResults = 0;

    public Config(String[] args) {

        if(args.length != 1){
            printHelp();
            System.exit(1);
        }

        if(args[0].equals("-h") || args[0].equals("--help")){
            printHelp();
            System.exit(0);
        }

        Logfile lf = new Logfile(args[0]);

        System.out.println(lf.size() + " entries read");

        Scanner in = new Scanner(System.in);

        System.out.print("% ");


        while (in.hasNextLine()) {

            String line = in.nextLine();

            // FIXME do we want to check for blank here
            if (line.isBlank()) {
                break;
            }

            char command = line.charAt(0);
            // Note: you can use this to extract the arguments for a command
            // String arguments = line.substring(2);

            // TODO process each of the command types
            switch (command) {

                case '#':
                    // this is a comment, nothing happens
                    break;

                //THIS CASE HANDLED THE QUIT COMMAND
                case 'q':
                    System.exit(0);
                    break;

                case 'h':
                    printHelp();
                    System.exit(1);
                    break;

                //THIS CASE HANDLES THE TIMESTAMP SEARCH COMMAND
                case 't':
                    if(line.substring(1).isBlank()){
                        System.err.println("invalid entry, ");
                    } else {
                        String raw_timestamp = line.substring(2);
                        String[] time_split = raw_timestamp.split("[|]");
                        ArrayList<String> times = new ArrayList<>();
                        for (String t : time_split) {
                            if (!t.isBlank()) {
                                times.add(t);
                            }
                        }
                        if (times.get(0).length()==14&&times.get(1).length()==14) {
                            long t1 = convertTimeToLong(times.get(0));
                            long t2 = convertTimeToLong(times.get(1));
                            System.out.println("Timestamps search: " + (lf.timestampSearch(t1,
                                    t2)) + " entries found");
                        }else{
                            System.err.println("invalid timestamp lengths, Retry entering");
                        }

                    }
                    timestampsearch = true;
                    break;

                //THIS CASE HANDLES THE MATCHING TIMESTAMP COMMAND
                case 'm':
                    if(line.substring(1).isBlank()){
                        System.out.println("invalid entry ");
                    } else {
                        long time = convertTimeToLong(line.substring(2));
                        System.out.println("Timestamp search: " + lf.matchingSearch(time) + " entries found");
                    }
                    matchingsearch = true;
                    break;

                // THIS CASE HANDLES THE CATEGORY SEARCH COMMAND
                case 'c':
                    if(line.substring(1).isBlank()){
                        System.out.println("invalid entry ");
                    } else {
                        String raw_category = line.substring(2).toLowerCase();
                        numResults = lf.categorySearch(raw_category);
                        System.out.println("Category search: " + numResults + " entries found");
                        categorysearch = true;
                        break;
                    }

                //THIS CASE HANDLES THE KEYWORD SEARCH COMMAND
                case 'k':
                    if(line.substring(1).isBlank()){
                        System.out.println("invalid entry ");
                    } else {
                        String raw_keywords = line.substring(2).toLowerCase();
                        String[] raw_split = raw_keywords.split("[^a-zA-Z0-9]+");
                        ArrayList<String> kwds = new ArrayList<>();
                        for (String s : raw_split) {
                            if (!s.isBlank()) {
                                kwds.add(s);
                            }
                        }
                        numResults = lf.keywordSearch(kwds);
                        System.out.println("Keyword search: " + numResults + " entries found");
                        keywordsearch = true;
                        break;
                    }

                //THIS CASE HANDLES THE APPEND LOG ENTRY COMMAND
                case 'a':
                    if(!line.substring(1).isBlank()){
                        int t = Integer.parseInt(line.substring(2));
                        if(lf.appendLogEntry(t) == 1) {
                            System.out.println("log entry " + t + " appended");
                        } else{
                            System.err.println("invalid index");
                        }
                    }
                    appendlogentry = true;
                    break;

                //THIS CASE HANDLES THE APPEND SEARCH RESULTS COMMAND
                case 'r':
                    // a previous search has to have occurred for r
                    if (!lf.previousSearch()){
                        System.err.println("No previous search occured");
                    } else {
                        int t = lf.appendSearchResults();
                        System.out.println(t + " log entries appended");
                    }
                    appendsearchresults = true;
                    break;

                //THIS CASE HANDLES THE DELETE LOG ENTRY COMMAND
                case 'd':
                    if(!line.substring(1).isBlank()) {
                        int d = Integer.parseInt(line.substring(2));
                        if (lf.deleteLogEntry(d) == 1) {
                            System.out.println("Deleted excerpt list entry " + d);
                        } else{
                            System.err.println("invalid index");
                        }
                    }
                    deletelogentry = true;
                    break;

                //THIS CASE HANDLES THE MOVE TO BEGINNING COMMAND
                case 'b':
                    if(!line.substring(1).isBlank()) {
                        int b = Integer.parseInt(line.substring(2));
                        if (lf.moveToBeginning(b) == 1) {
                            System.out.println("Moved excerpt list entry " + b);
                        } else{
                            System.err.println("invalid index");
                        }
                    }
                    movetobeginning = true;
                    break;

                //THIS CASE HANDLES THE MOVE TO END COMMAND
                case 'e':
                    if(!line.substring(1).isBlank()) {
                        int e = Integer.parseInt(line.substring(2));
                        if (lf.moveToEnd(e) > 0) {
                            System.out.println("Moved excerpt list entry " + e);
                        } else{
                            System.err.println("invalid index");
                        }
                    }
                    movetoend = true;
                    break;

                //THIS CASE HANDLED THE SORT EXCERPT LIST COMMAND
                case 's':
                    System.out.println("excerpt list sorted");
                    lf.sortExcerptList();
                    sortexcerptlist = true;
                    break;

                //THIS CASE HANDLED THE CLEAR EXCERPT LIST COMMAND
                case 'l':
                    System.out.println("excerpt list cleared");
                    lf.clearExcerptList();
                    clearexcerptlist = true;
                    break;

                //THIS CASE HANDLED THE PRINT MOST RECENT SEARCH RESULTS COMMAND
                case 'g':
                    // a previous search has to have occured for g and 3
                    if (!lf.previousSearch()){
                        System.err.println("No previous search occured");
                    } else {
                        lf.printRecentSearchResults();
                    }
                    pmrsearchresults = true;
                    break;

                //THIS CASE HANDLED THE PRINT EXCERPT LIST COMMAND
                case 'p':
                    lf.printExcerptResults();
                    pexcerptlist = true;
                    break;

                default:
                    System.err.println("Unexpected command " + command);

            } // switch
            System.out.print("% ");

        } // while

    }

    public static long convertTimeToLong(String time){
        // M M : D D : h h : m m  :  s  s
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13

        return (time.charAt(13) - '0') * 1L +
                (time.charAt(12) - '0') * 10L +
                (time.charAt(10) - '0') * 100L +
                (time.charAt(9) - '0') * 1000L +
                (time.charAt(7) - '0') * 10000L +
                (time.charAt(6) - '0') * 100000L +
                (time.charAt(4) - '0') * 1000000L +
                (time.charAt(3) - '0') * 10000000L +
                (time.charAt(1) - '0') * 100000000L +
                (time.charAt(0) - '0') * 1000000000L;

    }

    public static void printHelp(){
        System.out.println("Usage: Logman LOGFILE | -h | --help");
    }


}