public class Trader {
    private int traderId;

    public int getTraderId() {
        return traderId;
    }

    public int getStocksBought() {
        return stocksBought;
    }

    public int getStocksSold() {
        return stocksSold;
    }

    public int getNetSales() {
        return netSales;
    }

    private int stocksBought;
    private int stocksSold;
    private int netSales;

    public Trader(int id){
        traderId = id;
        stocksBought = 0;
        stocksSold = 0;
        netSales= 0;
    }

    public void buy(int shares, int price) {
        //updates the stocks bought and the shares by the traders
        //since we're buying then we need to subtract from the net sales
        stocksBought+=shares;
        netSales-= (price * shares);
    }

    public void sell(int shares, int price) {
        //updates the stocks sold and the shares by the traders
        //since we're buying then we need to add to the net sales
        stocksSold+=shares;
        netSales+=(price * shares);

    }
}
