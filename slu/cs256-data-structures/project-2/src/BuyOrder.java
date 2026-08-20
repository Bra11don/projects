public class BuyOrder extends Order{
    //hii nmetengeneza mwenyewe

    public BuyOrder(long id, long ts, int tId, int sId, int p, int q) {
        super(id, ts, tId, sId, p, q);
    }
    @Override
    public boolean isSell() {
        return false;
    }

    @Override
    public int compareTo(Order o) {
        //this
        //o --other object
        //return a negative int when this is higher priority than o

        //break ties for same price
        if(this.getPrice()==o.getPrice()){
            //the order that came first gets priority
            //smallerId means came earlier
            //same for buyOrder
            return (int) (this.getId() - o.getId());
        }

        return o.getPrice() -this.getPrice(); //if the other price is lower than our current price (negative answer) then we can buy
    }
}
