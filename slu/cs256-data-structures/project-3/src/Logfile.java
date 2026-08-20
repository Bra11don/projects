import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Logfile {

    private ArrayList<LogEntry> masterList;
    private ArrayList<Integer> indexMap;
    private ArrayList<LogEntry> excerptList = new ArrayList<>();

    //helper DS for indices
    private HashMap<String, ArrayList<Integer>> categoryMap;
    private HashMap<String, ArrayList<Integer>> keywordMap;

    // timestamp/matching search results
    private int startIdx, endIdx;
    ArrayList<Integer> matchingtimes;

    // category/keywrod search result
    private ArrayList<Integer> hashSearchResults;

    private LastSearch searchKind;

    public Logfile(String fn){
        searchKind = LastSearch.None;
        startIdx = endIdx = -1;
        masterList = new ArrayList<LogEntry>();
        int count = 0;

        try {
            Scanner in = new Scanner(new File(fn));

            while(in.hasNextLine()){
                String line = in.nextLine();
                if(line.isBlank()){
                    // empty line
                    break;
                }

                // do something here
                masterList.add(new LogEntry(line, count));
                count++;
            }

        } catch (FileNotFoundException e) {
            System.err.println(fn + " not found");
            System.exit(1);
        }

        // post-process the log entries
        postProcess();
    }

    /**
     * This method does all of the processing of the log entries
     * to prepare us for all of the user commands
     */
    private void postProcess() {
        SortingComparator comp = new SortingComparator();
        Collections.sort(masterList, comp);

        indexMap = new ArrayList<>(masterList.size());

        for(int i = 0; i < masterList.size(); i++) {
            // insert a dummy value
            indexMap.add(0);
        }

        // set the mapping for the original locations in the masterlog file
        for(int i = 0; i < masterList.size(); i++){
            LogEntry e = masterList.get(i);
            indexMap.set(e.getId(), i);
        }

        hashSearchResults = new ArrayList<>();
        matchingtimes = new ArrayList<>();
        prepCategorySearch();
        prepKeywordSearch();

    }

    /**
     * Combines all keywords from the masterlist in a keyword Hashmap which will be used in the keywordSearch
     * method to check if the keyword given exists in the map
     */
    private void prepKeywordSearch() {
        keywordMap = new HashMap<>();
        for(int i = 0; i < masterList.size(); i++){
            LogEntry e = masterList.get(i);

            // split up the message into keywords
            String[] kwds = e.getMessage().substring(15).toLowerCase().split("[^a-zA-Z0-9]+");

            for(String kwd: kwds){
                if(kwd.isBlank())
                    continue;

                // add the current index to the entry for the keyword
                if(!keywordMap.containsKey(kwd)){
                    keywordMap.put(kwd, new ArrayList<>());
                }

                // FIXME: what about duplicate keywords?
                keywordMap.get(kwd).add(i);

            }
        }
    }

    /**
     * Perform a keyword search on the log categories and log messages, and display the number of
     * matching entries
     * @param kwds - the given keywords
     * @return - all loge entries that contain every keyword
     */
    public int keywordSearch(ArrayList<String> kwds){
        searchKind = LastSearch.Keyword;
        hashSearchResults.clear();
        HashSet<Integer> setObject = new HashSet<>();

        if (keywordMap.get(kwds.get(0).toLowerCase())!=null){
            setObject.addAll(keywordMap.get(kwds.get(0).toLowerCase())); //using a hashset so that we can easily rule
            // out multiple keywords in the same log entry that are considered different things instead of the same
            // thing
        }

        for (int i = 1; i< kwds.size();i++){
            setObject.retainAll(keywordMap.getOrDefault(kwds.get(i).toLowerCase(), new ArrayList<>()));
        }
        hashSearchResults.addAll(setObject);
        Collections.sort(hashSearchResults);

        return hashSearchResults.size();
    }

    /**
     * Combines all categories from the masterlist in a category Hashmap which will be used in the categorySearch
     * method to check if the category given exists in the map
     */
    private void prepCategorySearch() {
        categoryMap = new HashMap<>();
        for(int i = 0; i < masterList.size(); i++){
            LogEntry e = masterList.get(i);

            // if category is in mapping
            if(!categoryMap.containsKey(e.getCategory())){
                categoryMap.put(e.getCategory(), new ArrayList<>());
            }

            categoryMap.get(e.getCategory()).add(i);
        }
    }

    /**
     * Searches for all log entries with categories matching <string> and displays the number of
     * matching entries.
     * @param cat - the category keyword
     * @return - the number of keywords with the given category
     */
    public int categorySearch(String cat){
        searchKind = LastSearch.Category;
        hashSearchResults.clear();

        // FIXME if the category doesn't exist, get returns null
        // we cannot addAll of null
        // we need to check that the category exists before doing this
        if(categoryMap.get(cat.toLowerCase()) != null) {
            hashSearchResults.addAll(categoryMap.get(cat.toLowerCase())); //once we have found the identical category
            // we add it to the hashsearch results arraylist
        } else{
            return hashSearchResults.size();
        }

        return hashSearchResults.size();
    }

    /**
     * The timestampSearch method Executes a search for all log entries with timestamps that fall within a specified
     * time range and displays the number of matching entries
     *
     * The LowerBound method compares two Logentries and returns 1 or -1 for which one is the earliest accprding to
     * timestamp
     * The UpperBound method compares two Logentries and returns 1 or -1 for which one is the latest according to
     * timestamp
     */
    private static class LowerBound implements Comparator<LogEntry>{

        public int compare(LogEntry o1, LogEntry o2){
            // the times
            if(o1.getTimestamp() < o2.getTimestamp()){
                return -1;
            } else{
                return 1;
            }
        }
    }

    private static class UpperBound implements Comparator<LogEntry>{

        public int compare(LogEntry o1, LogEntry o2){
            if(o1.getTimestamp() <= o2.getTimestamp()){
                return -1;
            } else{
                return 1;
            }
        }
    }

    /**
     * Conduct a timestamp search for entries in the range
     * @param start the starting timestamp
     * @param end the ending timestamp
     * @return the number of elements found by this search
     */

    public int timestampSearch(long start, long end){
        searchKind = LastSearch.Timestamp;
        matchingtimes.clear();

        LogEntry tmp1 = new LogEntry(start);
        // find the index of the starting timestamp
        int starting = Collections.binarySearch(masterList, tmp1, new LowerBound());

        startIdx = (starting + 1) * -1;
        // find the index of the ending timestamp and save it

        LogEntry tmp2 = new LogEntry(end);
        int ending = Collections.binarySearch(masterList, tmp2, new UpperBound());

        endIdx = (ending + 1) * -1;

        for(int i = startIdx; i<endIdx; i++){
            matchingtimes.add(i);
        }

        return endIdx - startIdx;
    }

    /**
     * Searches for all log entries with timestamps matching the given timestamp and displays the number of matching
     * entries.
     * @param timestamp
     * @return - the number of entries that match the provided timestamp
     */
    public int matchingSearch(long timestamp){
        searchKind = LastSearch.Matching;

        int mid = masterList.size() / 2;
        int left = 0;
        int right = masterList.size() - 1;
        int count = 0;

        matchingtimes.clear();

        while(left <= right) {
            if (timestamp < masterList.get(mid).getTimestamp()) {
                right = mid - 1;
            } else if(timestamp > masterList.get(mid).getTimestamp()){
                left = mid + 1;
            } else {
                count++;
                int tmp = mid;
                matchingtimes.add(mid);

                while(mid + 1 != masterList.size() && timestamp == masterList.get(mid + 1).getTimestamp()){
                    count++;
                    mid++;
                    matchingtimes.add(mid);
                }

                while(tmp - 1 != -1 && timestamp == masterList.get(tmp - 1).getTimestamp()){
                    count++;
                    tmp--;
                    matchingtimes.add(tmp);
                }
                break;
            }
            mid = (left + right) / 2;
        }

        return count;

    }

    /**
     * @return - the size of the logentries in the masterlist
     */
    public int size(){
        return masterList.size();
    }

    /**
     * Append the log entry from position <id> in the master log file onto the end of the excerpt
     * list.
     * @param id - position of the log entry in the masterlist
     * @return -  1 if the append was successful, -1 if the append was not successful
     */
    public int appendLogEntry(int id){
        if(id > masterList.size() - 1 || id < 0){
            return -1;
        } else {
            int i = 0;
            while(masterList.get(i).getId() != id){
                i++;
            }
            LogEntry foundId = masterList.get(i);
            excerptList.add(foundId);
            return 1;
        }
    }

    /**
     * Add all log entries returned by the most recent previous search (commands t, m, c, or k) to the
     * end of the excerpt list. Before the log entries are appended, they must be in order by timestamp,
     * with ties broken by category, and further ties broken by entryID.
     * @return - the number of entries appended or nothing if no previous search occured
     */
    public int appendSearchResults(){
        int count = 0;
        ArrayList<LogEntry> tmp = new ArrayList<>();
        SortingComparator comp = new SortingComparator(); //using the sorting comparator to break the ties
        if(searchKind.equals(LastSearch.Category) || searchKind.equals(LastSearch.Keyword)){
            for(int i = 0; i < hashSearchResults.size(); i++){
                tmp.add(masterList.get(hashSearchResults.get(i)));
                count++;
            }
            Collections.sort(tmp, comp);
            excerptList.addAll(tmp);
        }

        else if(searchKind.equals(LastSearch.Matching) || searchKind.equals(LastSearch.Timestamp)) {
            Collections.sort(matchingtimes);
            for(int i = 0; i < matchingtimes.size(); i++){
                tmp.add(masterList.get(matchingtimes.get(i)));
                count++;
            }
            Collections.sort(tmp, comp);
            excerptList.addAll(tmp);
        }
        return count;

    }

    /**
     * Remove the excerpt list entry at position <index>
     * @param index - the position of the log entry in the excerptlist
     * @return -  1 if the deletion is successful and -1 of deletion is unsuccessful
     */
    public int deleteLogEntry(int index){
        if(index >=0 && index < excerptList.size()){
            excerptList.remove(index);
            return 1;
        }
        return -1;
    }

    /**
     * Move the excerpt list entry at position <integer> to the beginning of the excerpt list
     * @param index - position of the logentry in te excerptlist
     * @return - 1 if moving is successful and -1 if moving did not happen
     */
    public int moveToBeginning(int index){
        if(index < excerptList.size()){
            LogEntry tmp = excerptList.get(index);
            excerptList.add(0,tmp);
            excerptList.remove(index+1);
            return 1;
        }
        return -1;
    }

    /**
     * Move the excerpt list entry at position <integer> to the end of the excerpt list
     * @param index - position of the logentry in te excerptlist
     * @return - 1 if moving is successful and -1 if moving did not happen
     */
    public int moveToEnd(int index){
        if(index >=0 && index < excerptList.size()){
            LogEntry tmp = excerptList.get(index);
            excerptList.add(excerptList.size(),tmp);
            excerptList.remove(index);
            return 1;
        }
        return -1;
    }

    /**
     * Sort each entry in the excerpt list by timestamp, with ties broken by category, and further ties
     * broken by entryID
     */
    public void sortExcerptList(){
        if (excerptList.size()==0){
            System.out.println("(previously empty)");
        }else{
            System.out.println("previous ordering:");
            System.out.println(0 + "|"+excerptList.get(0).getId()+"|"+excerptList.get(0).getMessage());
            System.out.println("...");
            System.out.println(excerptList.size()-1+"|"+excerptList.get(excerptList.size()-1).getId()+"|"+excerptList.get(excerptList.size()-1).getMessage());
            SortingComparator comp = new SortingComparator();
            Collections.sort(excerptList, comp);
            System.out.println("new ordering:");
            System.out.println(0 + "|"+excerptList.get(0).getId()+"|"+excerptList.get(0).getMessage());
            System.out.println("...");
            System.out.println(excerptList.size()-1+"|"+excerptList.get(excerptList.size()-1).getId()+"|"+excerptList.get(excerptList.size()-1).getMessage());
        }
    }

    /**
     * Remove all entries from the excerpt list
     */
    public void clearExcerptList(){
        if (excerptList.size()==0){
            System.out.println("(previously empty)");
        }else{
            System.out.println("previous contents:");
            System.out.println(0 + "|"+excerptList.get(0).getId()+"|"+excerptList.get(0).getMessage());
            System.out.println("...");
            System.out.println(excerptList.size()-1+"|"+excerptList.get(excerptList.size()-1).getId()+"|"+excerptList.get(excerptList.size()-1).getMessage());
            excerptList.clear();
        }
    }

    /**
     * Print the entries returned by the previous search, or nothing if no
     * previous search has occurred
     */
    public void printRecentSearchResults() {
        if(searchKind.equals(LastSearch.Category) || searchKind.equals(LastSearch.Keyword)){
            Collections.sort(hashSearchResults);
            for(int i = 0; i < hashSearchResults.size(); i++){
                System.out.println(masterList.get(hashSearchResults.get(i)).getId()+"|"+masterList.get(hashSearchResults.get(i)).getMessage());
            }
        }

        else if(searchKind.equals(LastSearch.Matching) || searchKind.equals(LastSearch.Timestamp) ) {
            Collections.sort(matchingtimes);
            for(int i = 0; i < matchingtimes.size(); i++){
                System.out.println(masterList.get(matchingtimes.get(i)).getId()+"|"+masterList.get(matchingtimes.get(i)).getMessage());
            }
        }

    }

    /**
     * Print the list of excerpt list entries in the excerpt list
     */
    public void printExcerptResults(){
        for (int i = 0; i< excerptList.size();i++){
            System.out.println(i+"|"+excerptList.get(i).getId()+ "|"+excerptList.get(i).getMessage());
        }
    }

    /**
     * This method is useful when we call the printSearchResults and appendSearchResults methods because it checks if
     * any previous search has
     * occurred
     * @return -  false if previousSearch has occurred, true if previous search occurred
     */
    public boolean previousSearch(){
        if(searchKind.equals(LastSearch.None)){
            return false;
        }
        return true;
    }

    private enum LastSearch{
        None,
        Timestamp,
        Category,
        Keyword,
        Matching
    }

}
// 04:25:21:54:22|12:11:20:12:12

//KEVIN'S COMMENTS
//SORTINGEXCERPTLIST
//make a logfile that contains
//use duplicate entry - to check if ties are broken correctly

//the problem -- same timestamp and same category -=but the issue is that they are ordered wrongly -- EVENTUALLY
// SOLVED IT