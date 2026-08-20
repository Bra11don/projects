public class Main {
    public static void main(String[] args) {
        //read command line arguments
        Config c = new Config (args);
        StockMarketSimulation s = new StockMarketSimulation(c);

        System.out.println("Processing orders...");

        s.simulate();

        Stock t = new Stock(c);
        t.processedstatement();

        if (c.traderInfo){
            s.traderinfostatement();
        }
    }
}
