import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 *
 * This class is used to create a special type of crossroads where the player can actually pick up
 * and drop items. Other crossroads which are not "special" are used to extend the places where characters
 * can move but the player cannot do any real actions there.
 *
 * The abbreviation "CR" stands for "crossroads".
 *
 * @author Luka Kralj
 * @version December 2017
 */
public class Crossroads{
    private String name;
    private ArrayList<Item> items; // Stores items that are at this crossroads.
    private Customer customer;

    // Maps string which is actually coordinates string in form ( x, y) to the crossroads that is in that location.
    private static HashMap<String, Crossroads> allCR = new HashMap<>();

    // Stores all coordinates string in form ( x, y). It is used for printing information where special crossroads are
    // when the user enters the command "map".
    private static ArrayList<String> allStrings = new ArrayList<>();

    /**
     * Creates a "specaial" crossroads at a specified location and adds items to it that are there at the beginning.
     *
     * @param name Name of a facility or a customer at this crossroads.
     * @param x A "user-friendly" coordinate: valid values are 0-4 inclusive.
     * @param y A "user-friendly" coordinate: valid values are 0-5 inclusive.
     * @param startingItems ArrayList of items that are at this crossroads at the beginning of the game.
     * @param customer Null if there is no customer that only requires a certain item.
     */
    public Crossroads(String name, int x, int y, ArrayList<Item> startingItems, Customer customer){
        this.name = name;
        items = startingItems;
        this.customer = customer;
        String locationString = makeLocationString(x, y);
        allCR.put(locationString, this);
        allStrings.add(locationString);
    }

    /**
     * Returns the crossroads that has the given coordinates. If there is no crossroads at this location it returns null.
     * @param x A "user-friendly" coordinate: valid values are 0-4 inclusive.
     * @param y A "user-friendly" coordinate: valid values are 0-5 inclusive.
     */
    public static Crossroads getCR(int x, int y){
        String locationString = makeLocationString(x, y);
        if(allCR.containsKey(locationString)){
            return allCR.get(locationString);
        }
        return null;
    }

    /**
     * Prints list of all the crossroads an their locations so the player know where the wanted crossroads are.
     */
    public static void printAllCR(){
        for(String loc : allStrings){
            String name = allCR.get(loc).getCRName();
            System.out.println(" - " + name + " is at the crossroads " + loc + ".");
        }
    }

    /**
     *
     * @param x A "user-friendly" coordinate: valid values are 0-4 inclusive.
     * @param y A "user-friendly" coordinate: valid values are 0-5 inclusive.
     * @return A location string which is used to identify each crossroads.
     */
    private static  String makeLocationString(int x, int y){
        return  "( " + x + ", " + y + ")";
    }

    /**
     * Removes the item from the crossroads (player picked it up).
     * @param item Item to be removed.
     */
    public void removeItemCR(Item item){
        items.remove(item);
    }

    /**
     * Adds the item to the crossroads (player dropped it).
     * @param item Item to be added.
     */
    public void addItemCR(Item item){
        items.add(item);
    }

    /**
     *
     * @return Returns an ArrayList of all the ites currently on the crossroads.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Prints all items that are currently at the crossroads.
     * If the player has not payed for an item the cost is displayed as well.
     */
    public void printCRItems(){
        for(Item i : items){
            if(i.isPaidFor()){
                System.out.println(" - " + i.getName());
            }
            else{
                System.out.println(" - " + i.getName() + " (£" + i.getCost() + ")");
            }

        }
    }

    /**
     *
     * @return Number of item that are currently at the crossroads.
     */
    public int numOfItems(){
        return items.size();
    }

    /**
     *
     * @return Name of a facility or a customer at this crossroads.
     */
    public String getCRName(){
        return name;
    }

    /**
     *
     * @return Customer object associated with this crossroads. Null if none.
     */
    public Customer getCustomer(){
        return customer;
    }
}
