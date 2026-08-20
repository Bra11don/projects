public abstract class Order implements Comparable<Order> {
    private long id; //unique number to describe the order
    private long timestamp;
    private int traderId;
    private int stockID;
    private int price;
    private int quantity;
    //by making them (the class Order adn method compareTo) abstract we're saying that we're not going to implement these (not going to be able to instantiate them)

    public Order(long id,long ts, int tId, int sId, int p, int q){
        this.id = id;
        timestamp = ts;
        traderId = tId;
        stockID = sId;
        price = p;
        quantity = q;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public int getTraderId() {
        return traderId;
    }

    public int getStockID() {
        return stockID;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public abstract boolean isSell();
    @Override
    public abstract int compareTo(Order o);

    /**
     * reduce the quantity of shares in this orders
     * @param shares - quantity t reduce by
     */
    public void removeShares(int shares) {
        quantity -=shares;
    }
}
