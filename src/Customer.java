import java.util.Random;

/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 * 
 * This class is used to create customers which are part of some special crossroads.
 * It stores the item the customer "ordered" and generates the payment for the completed delivery.
 *
 *
 * @author Luka Kralj
 * @version December 2017
 */
public class Customer {
    private Item orderedItem;
    private int payment;

    /**
     * Creates a customer and maps the "ordered" item to it.
     *
     * @param orderedItem An item the customer requires.
     */
    public Customer(Item orderedItem){
        this.orderedItem = orderedItem;

        Random rand = new Random();
        //Player gets tipped with the amount of up to 50% of actual parcel price.
        int tip = 1 + rand.nextInt(orderedItem.getCost()/2);
        //The payment for the parcel is actual price plus tips.
        payment = orderedItem.getCost() + tip;

    }

    /**
     * Compares the given item to the item a customer requires. If they are the same returns true, otherwise false.
     * @param item Item we want to check if it is a required item.
     */
    public boolean isRightParcel(Item item){
        if(orderedItem.equals(item)){
            return true;
        }
        return false;
    }

    /**
     * Returns the payment for the order.
     */
    public int getPayment(){
        return payment;
    }

    /**
     * Returns the item a customer has "ordered".
     */
    public Item getOrderedItem(){
        return orderedItem;
    }

}
