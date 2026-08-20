import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/*
store and process all configurations for this project
 */
public class Config {
    //need several member variables to store our settings
    //methods that access our settings later

    //check for stack vs queue mode
    private boolean stackMode;

    //morph modes
    private boolean changeMode;
    private boolean swapMode;
    private boolean length;

    //output mode
    private boolean wordOutput = true;

    //beginning word and ending word
    private String beginWord;
    private String endWord;

    //checkpoints
    private boolean checkpoint1;
    private boolean checkpoint2;


    //Construct our configuration object and process a command line arguments
    //@param args  is a string of command line arguments
    public Config (String[]args){

        //Getopt processing
        LongOpt[] longOptions = {
                new LongOpt("stack", LongOpt.NO_ARGUMENT, null, 's'),
                new LongOpt("queue", LongOpt.NO_ARGUMENT, null, 'q'),
                new LongOpt("change", LongOpt.NO_ARGUMENT, null, 'c'),
                new LongOpt("swap", LongOpt.NO_ARGUMENT, null, 'p'),
                new LongOpt("length", LongOpt.NO_ARGUMENT, null, 'l'),
                new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
                new LongOpt("begin", LongOpt.REQUIRED_ARGUMENT, null, 'b'),
                new LongOpt("end", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
                new LongOpt("help", LongOpt.NO_ARGUMENT,null, 'h'),
                new LongOpt("checkpoint1", LongOpt.NO_ARGUMENT, null, 'x'),
                new LongOpt("checkpoint2", LongOpt.NO_ARGUMENT, null, 'y')

        };

        Getopt g = new Getopt("Project1",args, "sqcplo:b:e:hxy", longOptions);
        g.setOpterr(true);

        int choice;

        //error checking variables
        boolean routingModeSet = false;


        int stackqueueCheck = 0;

        while ((choice = g.getopt())!= -1){
            switch (choice){
                case 's':
                    stackMode = true;
                    routingModeSet = true;
                    stackqueueCheck++;
                    break;
                case 'q':
                    stackMode = false;
                    routingModeSet = true;
                    stackqueueCheck++;
                    break;
                case 'c':
                    changeMode = true;
                    break;
                case 'p':
                    swapMode = true;
                    break;
                case 'l':
                    length = true;
                    break;
                case 'o':
                    //read the required string argument
                    String mode = g.getOptarg();
                    if (!mode.equals("M") && !mode.equals("W")) {
                        System.err.println("Only W and M are supported for modes " + mode);
                        System.exit(1);
                    }
                    wordOutput = mode.equals("W");

                    break;
                case 'b':
                    beginWord = g.getOptarg();
                    break;
                case 'e':
                    endWord = g.getOptarg();
                    break;

                case 'h':
                    printHelp();
                    break;
                case 'x':
                    checkpoint1 = true;
                    break;
                case 'y':
                    checkpoint2 = true;
                    break;

                default:
                    System.err.println("unknown command line argument: " +  choice);
                    System.exit(1);
                    break;
            }
        }

//        stackqueueCheck(args);

        //TODO check that all required arguments are provided
        //TODO check that only stack or queue mode is provided, and also once stack is entered then it can be entered only once
        if (!routingModeSet){
            System.err.println("One of stack or queue mode should be specified");
            System.exit(1);
        }

        if (stackqueueCheck!=1 ){
                System.err.println("only one of stack (-s) or queue (-q) should be specified");
                System.exit(1);
            }

        if (!(swapMode||changeMode||length)){
            System.err.println("atleast one morph argument (-c,-l,-s) should be specified");
            System.exit(1);
        }

        if (beginWord==null || endWord==null){
            System.err.println("missing begin word or end word");
            System.exit(1);
        }

        if (beginWord.length() != endWord.length() && !length) {
                System.err.println("length argument is required");
                System.exit(1);
            }

        if (beginWord.equals(endWord)){
            System.out.println("Words in morph: 1");
            System.out.println(beginWord);
            System.exit(1);
        }

    }
    public static void printHelp(){
        //TODO modify the printhelp function
        System.out.println(" the program creates a dictionary from a file that will be read from standard input ");
        System.out.println("It runs takes in an end word as an initial argument and generates a path from the beginning word to the end word according to the provided arguments. ");
        System.out.println("[--stack | -s] If this flag is set, use the stack-based routing scheme");
        System.out.println("[--queue | -q] If this flag is set, use the queue-based routing scheme.");
        System.out.println("[--change | -c] If this flag is set, Letterman is allowed to change one letter into another");
        System.out.println("[--swap | -p] If this flag is set, Letterman is allowed to swap any two adjacent characters.");
        System.out.println("[--length | -l] If this flag is set, Letterman is allowed to modify the length of a word, by\n" +
                "inserting or deleting a single letter.");
        System.out.println("[--output | -o] (W|M) Indicates the output file format by following the flag with a W\n" +
                "(word format) or M (modification format). If the --output option is not specified, default to\n" +
                "word output format (W). If --output is specified on the command line, the argument\n" +
                "(either W or M) to it is required");
        System.out.println("t[--begin | -b] <word> This specifies the word that Letterman starts with. This\n" +
                "flag must be specified on the command line, and when it is specified a word must follow\n" +
                "it.\n");
        System.out.println("[--end | -e] <word> This specifies the word that Letterman must reach. This flag\n" +
                "must be specified on the command line, and when it is specified a word must follow it.\n");
        System.out.println("<Dictionary File>");
        System.exit(1);
    }

//    public static void stackqueueCheck(String[] args) {
//        int s_track = 0;
//        int q_track = 0;
//
//        for (String str: args){
//            if (str.equals("-q") || str.equals("--queue")) {
//                q_track++;
//            }
//            else if (str.equals("-s") || str.equals("--stack")){
//                s_track++;
//            }
//            if (s_track + q_track > 1 ){
//                System.err.println("not allowed");
//                System.exit(1);
//            }
//        }
//    }


    public boolean isChangeMode() {
        return changeMode;
    }

    public boolean isSwapMode() {
        return swapMode;
    }

    public boolean isLengthMode() {
        return length;
    }

    public boolean isCheckpoint1() {
        return checkpoint1;
    }

    public boolean isCheckpoint2() {
        return checkpoint2;
    }

    public boolean isStackMode() {
        return stackMode;
    }

    public boolean isWordOutput() {
        return wordOutput;
    }

    public String getBeginWord() {
        return beginWord;
    }

    public String getEndWord() {
        return endWord;
    }

}
