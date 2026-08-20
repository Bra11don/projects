import java.util.Comparator;

public class SortingComparator implements Comparator<LogEntry>{

    @Override
    public int compare(LogEntry o1, LogEntry o2) {
        //FIXME compare by timestamp first (hint use <> instead of -, return -1 or 1)
        //FIXME next comare categories (compareTo)
        //FIXME finally compare by entry ID
        if(o1.getTimestamp() < o2.getTimestamp()){
            return -1;
        } else if (o1.getTimestamp() > o2.getTimestamp()){
            return 1;
        } else { // check category
            if(o1.getCategory().compareTo(o2.getCategory()) != 0){
                return o1.getCategory().compareTo(o2.getCategory());
            } else{ // check id
                return o1.getId() - o2.getId();
            }
        }
    }
}
