import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class Config {
    boolean verbose;
    boolean traderInfo;
    boolean median;

    public Config(String[] args) {
        //Getopt processing on args to set verbose,traderinfo and median
        LongOpt[] longOptions = {
                new LongOpt("verbose", LongOpt.OPTIONAL_ARGUMENT, null, 'v'),
                new LongOpt("median", LongOpt.OPTIONAL_ARGUMENT, null, 'm'),
                new LongOpt("trader-info", LongOpt.OPTIONAL_ARGUMENT, null, 'i'),
                new LongOpt("help", LongOpt.NO_ARGUMENT,null, 'h'),
        };

        Getopt g = new Getopt("Project2",args, "vmih", longOptions);
        g.setOpterr(true);

        int choice;

        while ((choice = g.getopt())!= -1){
            switch (choice){
                case 'v':
                    verbose = true;
                    break;
                case 'm':
                    median = true;
                    break;
                case 'i':
                    traderInfo = true;
                    break;
                case 'h':
                    printHelp();
                    break;
                default:
                    System.err.println("unknown command line argument: " +  choice);
                    System.exit(1);
                    break;
            }
        }
    }

    public static void printHelp(){
        //TODO modify the printhelp function
        System.out.println(" the program  simulates stock market trading ");
        System.out.println("Traders will sell and buy different stocks throughout the day. ");
        System.out.println("[--verbose | -v] An optional flag that indicates verbose output should be generated");
        System.out.println("[--median | -m] An optional flag that indicates median output should be generated");
        System.out.println("[--trader-info | -i] An optional flag that indicates that the Trader information output should be generated.");
        System.exit(1);
    }
}
