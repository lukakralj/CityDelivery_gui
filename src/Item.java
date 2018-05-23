/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 *
 * This class is used to create items with which a player can interact during the game.
 *
 * @author Luka Kralj
 * @version December 2017
 */
public class Item
{
    private String name;
    private int weight;
    private int cost;
    private boolean paidFor;
    private String whatToDo;
    private boolean isParcel;

    /**
     * Creates an item.
     *
     * @param name Name of item.
     * @param weight Weight of item.
     * @param cost Cost of item in pounds.
     * @param whatToDo Gives a hint what to do with this item.
     * @param isParcel If an item is a parcel to delivery this is set to true. It is needed to know whether to get the payment or not.
     */
    public Item(String name, int weight, int cost, String whatToDo, boolean isParcel){
        this.name = name;
        this. weight = weight;
        this.cost = cost;
        this.paidFor = false; //False until the player pays for this item.
        this.whatToDo = whatToDo;
        this.isParcel = isParcel;

    }

    /**
     * Returns the weight of the item.
     */
    public int getWeight(){
        return weight;
    }

    /**
     * Returns the name of the item.
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the cost of the item.
     */
    public int getCost(){
        return cost;
    }

    /**
     * Returns true if the item has been payed for and we do not need to pay again to pick it up.
     * Returns false if we have not payed for the item yet.
     */
    public boolean isPaidFor(){
        return paidFor;
    }

    /**
     * When player pays for the item this is needed so the player will not have to pay again next time.
     */
    public void payFor(){
        paidFor = true;
    }

    /**
     * Returns the hint of what task the player has to complete with this item.
     */
    public String getWhatToDo(){
        return whatToDo;
    }

    /**
     * Returns true if the item is a parcel (needs to be delivered to the customer).
     */
    public boolean isParcel(){
        return isParcel;
    }
}
