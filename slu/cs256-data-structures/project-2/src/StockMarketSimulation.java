import java.util.ArrayList;
import java.util.Scanner;

public class StockMarketSimulation {
    private ArrayList<Stock>stocks;
    private ArrayList<Trader>traders;
    private Config config;

    private int numTraders;
    private int numStocks;

    private int stockId;

    private Scanner in;
    private long curr_id = 0;
    public StockMarketSimulation(Config c){
        config = c;

        //read in initial configuration
        in = new Scanner(System.in);

        //what we have to read.
//        COMMENT: <COMMENT>
//        MODE: <INPUT_MODE>
//        NUM_TRADERS: <NUM_TRADERS>
//        NUM_STOCKS: <NUM_STOCKS>
        //skip over the comment
        in.nextLine();

        //throw away the header so we get the contents only
        in.next();
        String mode = in.next();

        in.next();
        numTraders = in.nextInt();

        in.next();
        numStocks = in.nextInt();

        //construct ALs with the correct capacity
        traders = new ArrayList<Trader>(numTraders);
        stocks = new ArrayList<Stock>(numStocks); //instantiating this

        //populating the stocks ArrayList
        int i=0;
        while(i<numStocks){
            stocks.add(new Stock(c));
            i++;
        }

        //populating the traders ArrayList
        int j=0;
        while(j<numTraders){
            traders.add(new Trader(j));
            j++;
        }

        //check for PR mode
        if (mode.equals("PR")){
            //what we're reading
//            RANDOM_SEED: <SEED>
//            NUMBER_OF_ORDERS: <NUM_ORDERS>
//            ARRIVAL_RATE: <ARRIVAL_RATE>
            in.next();
            int seed  = in.nextInt(); //starting point for the random number generator

            in.next();
            int numOrders = in.nextInt();

            in.next();
            int arrivalRate = in.nextInt();

            in = P2Random.PRInit(seed,numTraders,numStocks,numOrders,arrivalRate); //this returns a random generator that reads orders one at a time.
        }
    }

    public void simulate(){
        //implementing the main processing loop for simulation
        long currentTime = 0;

        while(in.hasNextLong()){
            Order nextOrder = getNextOrder();

            //error checking the timestamp
            if (nextOrder.getTimestamp() <0 || nextOrder.getTimestamp()<currentTime){
                System.err.println("Timestamp needs to be an increasing positive integer");
                System.exit(1);
            }

            if(nextOrder.getTimestamp() > currentTime){
                //implementing the respective operations when the timestamp changes
                //If the --median option is specified, print the median information
                if (config.median){
                    for (int i = 0; i<stocks.size();i++){
                        //i is the stock ID
                        Stock s = stocks.get(i);
                        if (s.getNumTransactions()>0) {
                            int median = s.getMedian();
                            //print this
                            //Median match price of Stock <STOCK_ID> at time <TIMESTAMP> is $<MEDPRICE>
                            System.out.println("Median match price of Stock "+ i + " at time "+ currentTime + " is $" + median);
                        }
                    }
                }
                //Set CURRENT_TIMESTAMP to be the new orders TIMESTAMP.
                currentTime = nextOrder.getTimestamp();
            }

            Stock s = stocks.get(nextOrder.getStockID());
            //add the order to the stock
            s.addOrder(nextOrder);
            //perform matches
            s.performMatches(traders);
        }
        if (config.median) {
            for (int i = 0; i < stocks.size(); i++) {
                //i is the stock ID
                Stock s = stocks.get(i);
                if (s.getNumTransactions() > 0) {
                    int median = s.getMedian();
                    //print this
                    //Median match price of Stock <STOCK_ID> at time <TIMESTAMP> is $<MEDPRICE>
                    System.out.println("Median match price of Stock " + i + " at time " + currentTime + " is $" + median);
                }
            }
        }

    }

    public void traderinfostatement(){
        System.out.println("---Trader Info---");
        //print this
        //has some rules in this.. so Ill need to refer to doc
        // Trader <TRADER_ID> bought <NUMBER_BOUGHT> and sold <NUMBER_SOLD> for a net transfer of $<NET_VALUE_TRADED><NEWLINE>
        for (int i = 0; i< traders.size();i++) {
            Trader t = traders.get(i);
            System.out.println("Trader " + i + " bought " + t.getStocksBought() + " and sold " + t.getStocksSold() + " for a net transfer of $" + t.getNetSales());
        }
    }

    /**
     * Read and return the next order form in
     * @return Order Object with the next order to process
     */
    private Order getNextOrder(){
        //this is in TL mode
//        <TIMESTAMP> <BUY/SELL> T<TRADER_ID> S<STOCK_NUM> $<PRICE> #<QUANTITY>
        long ts = in.nextLong();
        String intent = in.next();
        int traderId = Integer.parseInt(in.next().substring(1)); //so that we skip the 'T'
        stockId = Integer.parseInt(in.next().substring(1));
        int price = Integer.parseInt(in.next().substring(1));
        int qty = Integer.parseInt(in.next().substring(1));

        //error checking for all the entries
        if (ts<0){
            System.err.println("timestamp must be a non negative number");
            System.exit(1);
        }

        if (price<1 ){
            System.err.println("Price must be positive integers");
            System.exit(1);
        }
        if (qty<1){
            System.err.println("Quantity must be positive integers");
            System.exit(1);
        }

        if (traderId<0 || traderId >= numTraders){
            System.err.println("traderId needs to be in range [0,numtraders)");
            System.exit(1);
        }

        if (stockId<0 || stockId >= numStocks){
            System.err.println("stockId needs to be in range [0,numstocks)");
            System.exit(1);
        }

        if (intent.equals("SELL")){
            return new SellOrder(curr_id++,ts,traderId,stockId,price,qty);
        }else{
            //FIXME BUY ORDER -- (nmeifanya chini hapa)
            return new BuyOrder(curr_id++,ts,traderId,stockId,price,qty);
        }

    }


}
