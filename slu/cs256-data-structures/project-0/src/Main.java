import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner user = new Scanner(System.in);

        //quick way to round numbers
        //private/public static final Decimal Format df = new Decimal Format(0.00)

        //have the user input total data Points
        int tdP = user.nextInt();

        //user input into an arraylist
        ArrayList<Float> data =new ArrayList<>(tdP);
        for (int i = 0; i<tdP;i++){
            data.add(user.nextFloat());
        }

        //checking if input is greater than the specified total data points above
        if (user.hasNextFloat()){
            System.err.println("Data input size exceeding specified total data points");
            System.exit(1);
        }

        //if there's no input
        if (tdP == 0 ){
            System.out.println("No data => no statistics!");
            System.exit(0);
        }

        LongOpt[] longOptions = {
                new LongOpt("help", LongOpt.NO_ARGUMENT,null, 'h'),
                new LongOpt("mode", LongOpt.REQUIRED_ARGUMENT, null, 'm'),
                new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v')
        };

        Getopt g = new Getopt("Project0",args, "hvm:", longOptions);
        g.setOpterr(true);

        int choice;

        String mode = "";

        float median = 0;
        double average = 0;

        while ((choice = g.getopt())!= -1){
            switch (choice){
                case 'h':
                    printHelp();
                    System.exit(0);
                    break;
                case 'v':
                    System.out.println("Reading "+ tdP + " numbers." );
                    System.out.println("Read "+ tdP + " numbers." );
                    break;
                case 'm':
                    mode = g.getOptarg(); //reads the next argument from the command line
                    if (!mode.equals("average")&& !mode.equals("median")){
                        System.err.println("Error: invalid mode: " +  mode);
                        System.exit(1);
                    }
                    if (mode.equals("average")){
                        double sum = 0;
                        for(double num:data)
                            sum += num;
                        average = (sum / data.size());
                    }
                    if (mode.equals("median")){
                        Collections.sort(data);
                        // check if total number of scores is even
                        if (data.size() % 2 == 0) {
                            // calculate average of middle elements
                            float middle = (data.get(data.size()/2) + data.get(data.size()/2 - 1));
                            median = middle/2;
                        } else {
                            // get the middle element
                            median = data.get(data.size() / 2);
                        }
                    }
                    break;
                default:
                    System.err.println("Error: invalid option");
                    System.exit(1);
                    break;
            }
        }

        if (mode.equals("")){
            System.err.println("Missing --mode/-m flag on the command line");
            System.exit(1);
        } //this method is taking longer and using more space

        //instead you can try
//        int mFlag = Arrays.binarySearch(args, "-m");
//         int modeFlag = Arrays.binarySearch(args, "--mode")
//        if (mFlag<0 && mFlag<0){
//            System.err.println();
//            System.exit(1);
//        }

        if (median == 0){
            System.out.println("Average: "+ (Math.round(average * 100.0)/100.0));
            //System.out.println("Median: " + df.format(average);
        }
        else{
            System.out.println("Median: " + (Math.round(median * 100.0)/100.0));
            //System.out.println("Median: " + df.format(median);
        }

    }

    public static void printHelp(){
        System.out.println("Usage: java[options] Main [-m average |median]|{-h}");
        System.out.println("This program is an example of processing command line arguments with getopt");
        System.out.println();
    }
}
