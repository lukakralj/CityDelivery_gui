import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        createSpecialCR();
        new MainWindow("Testing player");
    }

    private static void createSpecialCR() {
        // Create shop.
        String textTyre = "TASK: Take this tyre to the mechanic so he can change it for you.<br>";
        Item tyre = new Item("Tyre", 4, 25, textTyre, false);
        ArrayList<Item> shopItems= new ArrayList<>();
        shopItems.add(tyre);
        new Crossroads("Shop", 3, 2, shopItems, null);

        // Create mechanic.
        ArrayList<Item> mechanicItems = new ArrayList<>();
        // Mechanic behaves similarly as a customer. Since the item "Tyre" is marked as not being a parcel the player will not get payed at the "delivery".
        Customer mechanicCust = new Customer(tyre);
        new Crossroads("Mechanic", 4, 1, mechanicItems, mechanicCust);

        // Create ATM.
        ArrayList<Item> atmItems = new ArrayList<>();
        new Crossroads("ATM", 0, 3, atmItems, null);

        // Create Delivery Shop.
        String textParcelOne = "TASK: Take this parcel to Customer-1.<br>";
        String textParcelTwo = "TASK: Take this parcel to Customer-2.<br>";
        String textParcelThree = "TASK: Take this parcel to Customer-3.<br>";
        Item parcelOne = new Item("Parcel-1", 1, 10, textParcelOne, true);
        Item parcelTwo = new Item("Parcel-2", 2, 20, textParcelTwo, true);
        Item parcelThree = new Item("Parcel-3", 3, 30, textParcelThree, true);
        ArrayList<Item> deliveryItems = new ArrayList<>();
        deliveryItems.add(parcelOne);
        deliveryItems.add(parcelTwo);
        deliveryItems.add(parcelThree);
        new Crossroads("Delivery Shop", 0, 2, deliveryItems, null);

        // Create home.
        ArrayList<Item> homeItems = new ArrayList<>();
        new Crossroads("Home", 1, 1, homeItems, null);

        // Create 3 customers.
        ArrayList<Item> custOneItems = new ArrayList<>();
        ArrayList<Item> custTwoItems = new ArrayList<>();
        ArrayList<Item> custThreeItems = new ArrayList<>();
        Customer customerOne = new Customer(parcelOne);
        Customer customerTwo = new Customer(parcelTwo);
        Customer customerThree = new Customer(parcelThree);
        new Crossroads("Customer 1", 3, 0, custOneItems, customerOne);
        new Crossroads("Customer 2", 4, 5, custTwoItems, customerTwo);
        new Crossroads("Customer 3", 2, 3, custThreeItems, customerThree);
    }
}
