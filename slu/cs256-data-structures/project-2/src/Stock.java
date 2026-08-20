import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Stock {
    private Config config;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    public static int count;

    //TODO 2 more PQs for tracking median
    //min PQ
    PriorityQueue<Integer> topHalf = new PriorityQueue<>();
    //max PQ
    PriorityQueue<Integer> bottomHalf = new PriorityQueue<>(Collections.reverseOrder());

    //track median
    private int median ;//median keeping track of the price that we sold at
    private int numTransactions;

    public Stock(Config c){
        config = c;
        sellOrders = new PriorityQueue<>();
        buyOrders = new PriorityQueue<>();
    }

    public void addOrder(Order o){
        //TODO implement adding an order
        if (o.isSell()){
            sellOrders.add(o);
        }else {
            buyOrders.add(o);
        }
    }

    public void performMatches(List<Trader> traders){
        //TODO check if there are orders that can be matched and perform those transactions
        //checking if there are orders that can be matched and perform those transactions
        while (canMatch()){
            Order buy = buyOrders.peek();
            Order sell = sellOrders.peek();

            int price;
            if (buy.getId()<sell.getId()){
                price = buy.getPrice();
            }else{
                price = sell.getPrice();
            }


            int shares = Math.min(buy.getQuantity(), sell.getQuantity());

            //perform the transaction
            buy.removeShares(shares);
            sell.removeShares(shares);

            if (buy.getQuantity() == 0){
                buyOrders.remove();
            }

            //TODO sell orders --- (nmefanya hapa chini)
            if (sell.getQuantity()==0){
                sellOrders.remove();
            }

            // our output when verbose is enabled
            //Trader <BUYING_TRADER_NUM> purchased <NUM_SHARES> shares of Stock <STOCK_NUM>
            // from Trader <SELLING_TRADER_NUM> for $<PRICE>/share
            if (config.verbose) {
                System.out.println("Trader " + buy.getTraderId() + " purchased " +
                        shares + " shares of Stock " + buy.getStockID() + " from Trader "
                        + sell.getTraderId() + " for $" + price + "/share");
            }
            count++;

            //we need to update the median everytime we have a transaction
            updateMedian(price);

            numTransactions++;

            //update trader information
            traders.get(buy.getTraderId()).buy(shares,price);
            traders.get(sell.getTraderId()).sell(shares,price);


        }

    }

    private void updateMedian(int price) {
        //TODO add this price to the running median
            if (topHalf.isEmpty()&& bottomHalf.isEmpty()){
                //first item
                topHalf.add(price);
                median = price;
            }else{
                //decide if we are inserting in the top or bottom half
                if (price<median){
                    bottomHalf.add(price);
                }
                else{
                    topHalf.add(price);
                }
                //check for balanced sizes
                if (bottomHalf.size() - topHalf.size() == 2){
                    //shift an item from the bottom to the top
                    topHalf.add(bottomHalf.remove());
                }else if(topHalf.size() - bottomHalf.size() == 2){
                    bottomHalf.add(topHalf.remove());
                }

                //update the median value
                //sizes differ by atmost 1 -- data is odd
                if (bottomHalf.size()>  topHalf.size()){
                    median = bottomHalf.element();
                }else if(topHalf.size()> bottomHalf.size()){
                    median = topHalf.element();
                }else{
                    //integer division..
                    //this means that our data is even
                    median = (topHalf.element() + bottomHalf.element())/2;
                }
            }
    }

    //checking if two orders can be matched at the head of the PQs
    private boolean canMatch(){
        //check if two orders can be matched at the head of the PQs
        if (buyOrders.isEmpty()||sellOrders.isEmpty()){
            //we cannot match
            return false;
        }
        return sellOrders.peek().getPrice()<= buyOrders.peek().getPrice();
    }

    public int getMedian(){
        return median;
    }

    //we need to get the number of transactions
    public int getNumTransactions(){
        return numTransactions;
    }

    //constant print output at the end of the program
    public void processedstatement(){
        System.out.println("---End of Day---");
        System.out.println("Orders Processed: " + count ); // should be in this format --  Orders Processed: <ORDERS_PROCESSED><NEWLINE>
    }

}
