import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 *
 * This class controls the behaviour of a player which could be seen as an extension to
 * the class Characters. It uses a field "chr" of type Characters to communicate to the
 * map.
 *
 * When we create an object of this class two other characters (that move on their own)
 * are created. The random movement of these two characters is controlled in this class
 * because it depends on the number of moves a player makes. However, moves are then
 * executed in the class Characters.
 *
 * @author Luka Kralj
 * @version December 2017
 */
public class Player {
    // Available money on a bank account.
    private int bankBalance;
    // Available money in cash.
    private int cash;
    // The items the player currently has.
    private ArrayList<Item> items;
    // Current special crossroads the player is at. If the crossroads is not special the value is null.
    private Crossroads currentCR;
    //The class player uses this field to communicate to the map and respond according to other characters as well.
    private Characters chr;
    
    // Additional characters are stored in a field because method randomlyMoveChars()
    // need to have access to these characters
    private Characters chrWorkers; // Represents workers on duty. Player cannot go there as the crossroads is being maintained.
    private Characters chrMarket; // Represents market. Player cannot go there as there is an event going on.
    
    // Fields to keep control over the flow of the game (they get changed regularly in various methods in this class):

    // Holds the number of tasks the player has yet to complete. Once it reaches zero the player has fulfilled the conditions to win.
    private int tasksToComplete;
    // True if the player has made too many moves (the car needs a new tyre).
    private boolean requiresRepair;
    // Number of moves until the Repair will be required.
    private int movesToRepair;
    // Number of moves the player has before he gets stuck.
    private int movesLeft;
    private MainWindow wnd;


    /**
     * Create new player, add its name and place it to the starting position (x, y).
     * 
     * @param x A "user friendly" coordinate: valid values are 0-4 inclusive.
     *          Row in which we want the player to be (counting from top to bottom).
     * @param y A "user friendly" coordinate: valid values are 0-5 inclusive.
     *          Column in which we want the player to be (counting from left to right).
     */
    public Player(String name, int x, int y, MainWindow wnd){
        this.wnd = wnd;
        chr = new Characters(name, x, y);
        createOtherChars();
        //Starting balance.
        bankBalance = 50;
        cash = 0;
        // Correctly initialise all the fields for the correct flow of the game.
        currentCR = Crossroads.getCR(x, y);
        items = new ArrayList<>();
        tasksToComplete = 3;
        requiresRepair = false;
        movesToRepair = 30;
        movesLeft = 45;
    }

    /**
     * This method creates other characters that are going to be involved in the game.
     * Their starting positions is randomly selected using randomly selected integer coordinates.
     */
    private void createOtherChars(){
        int workersX = 1;
        int workersY = 1;
        // If the selected crossroads is either the players home, the Delivery Shop or an ATM we need to repeat
        // the selections as otherwise the player might get stuck too easily.
        while (workersX == 1 && workersY == 1 || workersX == 0 && workersY == 2 || workersX == 0 && workersY == 3){
            workersX = produceRandX();
            workersY = produceRandY();
        }
        chrWorkers = new Characters("Workers", workersX, workersY);

        int marketX = 1;
        int marketY = 1;
        // If the selected crossroads is either the players home, the Delivery Shop, an ATM or Workers we need to repeat
        // the selections as otherwise the player might get stuck too easily.
        while (marketX == 1 && marketY == 1 || marketX == 0 && marketY == 2 || marketX == 0 && marketY == 3 || marketX == workersX && marketY == workersY){
            marketX = produceRandX();
            marketY = produceRandY();
        }
        chrMarket = new Characters("Circus", marketX, marketY);
    }

    /**
     * This method controls picking up the item by the player. It check whether the maximum weight would be exceeded
     * and whether the item has already been payed for and acts accordingly.
     * @param item Item that the player wants to take.
     */
    public void takeItem(Item item){
        if(item.isPaidFor() && getTotalWeight() + item.getWeight() <= 5){
            // Removes item from the crossroads and adds it to the player.
            items.add(item);
            currentCR.removeItemCR(item);
            // Prints hint what to do with the item.
            System.out.println(item.getWhatToDo());
        }
        else {
            //Allowed weigth is 5.
            if(getTotalWeight() + item.getWeight() > 5){
                showMessage("You already carry too many things!");
                return;
            }
            // Player need to buy the item.
            String message = "<html>The item \"" + item.getName() + "\" will cost you £" + item.getCost() + ".<br><br>" +
                    "Would you like to buy this item?<br></html>";
            
            // If player does not wish to buy the item we simply exit the method.
            if(!getAnswer(message)){
                return;
            }
            
            // Checks if player has enough money.
            if(cash < item.getCost()){
                showMessage("<html>You don't have enough cash to buy this item.<br>Find an ATM and withdraw some money first.</html>");
            }
            else {
                cash -= item.getCost(); // Takes away the amount of money the player has to pay.
                // Removes item from the crossroads and adds it to the player.
                items.add(item);
                currentCR.removeItemCR(item);
                item.payFor(); // Change the item's field so the player will not have to pay again for this item.
                String mes = "<html>Purchase complete.<br><br>" + item.getWhatToDo() + "</html>";
                showNiceMessage(mes);
            }
        }
    }

    /**
     * This method controls dropping the item by the player. If there is a customer at a certain crossroads it checks
     * whether the dropped item is the right item to drop there.
     * @param item Item that the player wants to drop.
     */
    public void dropItem(Item item){
        String message = "<html>";
        if(currentCR.getCustomer()==null){
            // Remove the item from the player and adds it to the crossroads.
            items.remove(item);
            currentCR.addItemCR(item);
        }
        else if(item.isParcel()){
            if(currentCR.getCustomer().isRightParcel(item)){
                items.remove(item); // Remove item from the player. This item is not used anymore.
                bankBalance += currentCR.getCustomer().getPayment(); // Transfer the payment to the player's bank account.
                tasksToComplete--; // Parcel successfully delivered means task successfully completed.
                message = message + "Congratulations! Package successfully delivered.<br>Money has been transferred to your bank account.</html>";
                showNiceMessage(message);
            }
            else{
                // If the wrong item was delivered the player is told which item is required.
                message = message + "This customer didn't order this.<br>You need to deliver \"" + currentCR.getCustomer().getOrderedItem().getName() + "\".</html>";
                showMessage(message);
            }

        }
        else{
            if(currentCR.getCustomer().isRightParcel(item)){
                items.remove(item); // Remove item from the player. This item is not used anymore.
                requiresRepair = false;
                movesLeft = 50; // Reset available moves.
                movesToRepair = -1; // In case the car is repaired earlier than needed we do not need these counter again
                // (from now on it will never reach 0, as these is the only comparison done with this field).
                message = message + "Car repaired.</html>";
                showNiceMessage(message);
            }
            else{
                message = message + "If you want your car repaired you need to bring a \"" + currentCR.getCustomer().getOrderedItem().getName() + "\".</html>";
                showMessage(message);
            }
        }
        if(!requiresRepair && tasksToComplete == 0){
            // When all the tasks are completed the player can wander around a bit the game will end once they reach home or get stuck.
            showNiceMessage("<html>Congratulations!<br>You have completed all the tasks. Now you can go home.</html>");
        }
    }

    /**
     * This method is invoked when the player wants to withdraw money.
     * @param amount Amount of money the player wants to withdraw.
     */
    public void takeCash(int amount){
        cash+=amount;
        bankBalance-=amount;
    }

    /**
     * This method is invoked when the player wants to deposit money.
     * @param amount Amount of money the player wants to deposit.
     */
    public void dropCash(int amount){
        cash-=amount;
        bankBalance+=amount;
    }

    /**
     *
     * @return Returns ArrayList of the player's current items.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     *
     * @return Returns total weight of the player's current items.
     */
    private int getTotalWeight(){
        int total = 0;
        for(Item i : items){
            total += i.getWeight();
        }
        return total;
    }

    /**
     * This method sets new crossroads to the player and, if the crossroads is "special" it gives the player some
     * information about what kind of crossroads is there and what items are available there.
     */
    public void setNewCR(){
        //Set new crossroads to the player. If the crossroads is not special it will set null.
        currentCR = Crossroads.getCR(getX(), getY());
        String text = null;
        if(currentCR!= null){
            // Information about the "special" crossroads.
            text = "<html>There is " + currentCR.getCRName() + " at this crossroads.</html>";
        }
        MainWindow.updateWarnings(text);
    }

    /**
     *
     * @return Returns the crossroads the player is currently add.
     */
    public Crossroads getCurrentCR(){
        return currentCR;
    }

    /**
     *
     * @return Returns a "user-friendly" coordinate x.
     */
    public int getX(){
        return chr.getCurrentX()/2;
    }

    /**
     *
     * @return Returns a "user-friendly" coordinate y.
     */
    public int getY() {
        return chr.getCurrentY()/2;
    }

    /**
     * This method tries to change the player's position. If unsuccessful it prints the corresponding warning.
     */
    public void goUp(){
        int status = chr.goUp(); // Tries to move the player and returns the warning reference number.
        if(status != 4){
            // Unsuccessful move.
            MainWindow.updateWarnings(getWarning(status));
        }
        else{
            // Status code is 4. The move is allowed.
            setNewCR();
            movesToRepair--;
            movesLeft--;
            // Check if the conditions to end the game are fulfilled.
            checkConditionsToEnd();
        }
    }

    /**
     * This method tries to change the player's position. If unsuccessful it prints the corresponding warning.
     * goDown method is a bit different from other moving methods because it can lead into the entering to The Underground
     * Corridor System.
     */
    public void goDown(){
        int status = chr.goDown(); // Tries to move the player and returns the warning reference number.
        if(status == 2){
            //Player might want to enter the underground.
            underground();
        }
        else if(status != 4){
            MainWindow.updateWarnings(getWarning(status));
            return;
        }
        else{
            // Status code is 4. The move is allowed.
            setNewCR();
        }
        movesToRepair--;
        movesLeft--;
        // Check if the conditions to end the game are fulfilled.
        checkConditionsToEnd();
    }

    /**
     * This method tries to change the player's position. If unsuccessful it prints the corresponding warning.
     */
    public void goLeft(){
        int status = chr.goLeft(); // Tries to move the player and returns the warning reference number.
        if(status != 4){
            // Unsuccessful move.
            MainWindow.updateWarnings(getWarning(status));
        }
        else{
            // Status code is 4. The move is allowed.
            setNewCR();
            movesToRepair--;
            movesLeft--;
            // Check if the conditions to end the game are fulfilled.
            checkConditionsToEnd();
        }
    }

    /**
     * This method tries to change the player's position. If unsuccessful it prints the corresponding warning.
     */
    public void goRight(){
        int status = chr.goRight(); // Tries to move the player and returns the warning reference number.
        if(status != 4){
            // Unsuccessful move.
            MainWindow.updateWarnings(getWarning(status));
        }
        else{
            // Status code is 4. The move is allowed.
            setNewCR();
            movesToRepair--;
            movesLeft--;
            // Check if the conditions to end the game are fulfilled.
            checkConditionsToEnd();
        }
    }

    /**
     *
     * @param status Status code after the moveing attempt.
     * @return String which is the warning message when the move did not succeed.
     */
    private String getWarning(int status){
        HashMap<Integer, String> warnings = new HashMap<>();
        warnings.put(-1, "You are not a ghost! Try going around that building.");
        warnings.put(0, "Don't go there - it might be dangerous! Or the path is just closed...");
        warnings.put(1, "<html>No one knows what's beyond the city border... spooky.<br>Try another direction!</html>");
        warnings.put(3, "<html>Oops! Looks like something is going on on that crossroads.<br>Try another direction!<html>");

        return warnings.get(status);
    }

    /**
     * This method checks if the current situation satisfies any of the conditions to end the game - player can
     * either win or lose.
     *
     * If no such conditions are satisfied it randomly moves some other characters (not the player) around the city.
     */
    private void checkConditionsToEnd(){
        // We first need to check if the car needs repairs as this method is called right after moving of the player.
        wnd.updateMap();
        checkNeedsRepair();
        if(currentCR != null && currentCR.getCRName().equals("Home") && !requiresRepair && tasksToComplete == 0){
            String message = "<html>";
            if(cash + bankBalance >= 20){
                // If player is at Home, with a working car, all tasks completed and enough money - he won.
                message = message + "Congratulations! You won! You have enough money for a 20-pound dinner after such a hardworking day!<br><br>";
            }
            else{
                // If player is at Home, with a working car, all tasks completed but not enough money - he lost.
                message = message + "You lost! You have completed all the tasks but you have spent too much money.<br><br>";
            }
            message = message + "Thank you for playing. See you next time!</html>";
            showNiceMessage(message);
            // Exit application.
            System.exit(0);
        }
        else if(movesLeft == 0){
            //If the player runs out of moves he cannot finish the game and the game is over.
            String message = "<html>You got stuck! You have no more moves. Game over.<br><br>" +
                    "Better luck next time!</html>";
            showMessage(message);
            // Exit application.
            System.exit(0);
        }
        // Other characters (not the player) can be moved every 3 player's moves.
        else if(currentCR == null && movesLeft % 3 == 0){
            // If the player is at the any not "special" crossroads other characters can move.
            randomlyMoveChars();
        }
        else if (currentCR != null && !currentCR.getCRName().equals("ATM") && movesLeft % 3 == 0){
            // If the player is at a "special" crossroads that is NOT ATM then other characters can move.
            // If the characters could move after the player has moved to ATM they might block the character and the
            // would not be able to end in a "good" manner (exit code 0).
            randomlyMoveChars();
        }
    }

    /**
     * This method check whether the player has reached certain amount of moves that would lead to needing to repair the car.
     * The player will need to repair the car at most once per game.
     */
    private void checkNeedsRepair(){
        if(movesToRepair == 0){
            requiresRepair = true;
            movesToRepair--; // Sets movesToRepair to -1 and then this value keep decreasing (it is not used anymore).
            String message = "<html>You have been driving so long that your tyre has gone flat!<br>" +
                    "Buy a new one and go to the Mechanic.<br><br>" +
                    "You have " + movesLeft +" moves before your car gets completely stuck.</html";
            showMessage(message);
        }
    }

    /**
     * This method randomly moves ONE of the other characters (not player) into one of the four directions.
     * Note: if the move is unsuccessful (end of city, buildin...) the character will remain on the same place with
     * no message to the System.out.
     */
    private void randomlyMoveChars(){
        HashMap<Integer, Runnable> moves = new HashMap<>();
        moves.put(0, () -> chrWorkers.goUp());
        moves.put(1, () -> chrWorkers.goDown());
        moves.put(2, () -> chrWorkers.goLeft());
        moves.put(3, () -> chrWorkers.goRight());
        moves.put(4, () -> chrMarket.goUp());
        moves.put(5, () -> chrMarket.goDown());
        moves.put(6, () -> chrMarket.goLeft());
        moves.put(7, () -> chrMarket.goRight());

        Random rand = new Random();
        moves.get(rand.nextInt(8)).run();
    }

    /**
     * This method asks the user whether they want to enter the underground or not.
     */
    private void underground(){
        String message = "<html>This is the entrance to The Underground Corridors.<br><br>" +
                "Do you really want to enter The Underground Corridor System?<br>";

        // If player does not wish to enter we simply exit the method.
        if(!getAnswer(message)){
            return;
        }

        try{
            // The game pauses for two seconds which simulates walking through The Underground Corridor Systems.
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException err){
            System.out.println("Waiting interrupted.");
        }

        undergroundExit(); // Actually executes random alocation.
    }

    /**
     * This method determines the new position of the player after entering The Underground Corridor System.
     * It informs the player of the new location and whether he has been robbed or not.
     */
    private void undergroundExit(){
        boolean changed = false;
        while (!changed){
            int randX = produceRandX();
            int randY = produceRandY();
            // Tries to move the player to randomly chosen coordinates. If that crossroads is not empty it returns
            // false without any changes. Otherwise it moves the player and returns true.
            changed = chr.tryChangeCR(getX(), getY(), randX, randY);
        }

        int x = getX();
        int y = getY();

        wnd.updateMap();

        if(x == 4 && y == 0 || x == 0 && y == 4 || x == 0 && y == 5){
            // If the player happens to exit in one of the "dangerous" crossroads (surrounded by closed paths on map),
            // the game is over.
            String message = "<html>OOPS! Unfortunately you exited in a very dangerous area where no one can help you.<br>" +
                "Game over.<br><br>" +
                "Better luck next time!</html";
            showMessage(message);
            // Exit application.
            System.exit(0);
        }
        boolean robbed = gotRobbed(); // Determines if the player was robbed.
        if(robbed){
            showMessage("Oops! You got robbed. Be more careful next time.");
        }
        else{
            showNiceMessage("You managed to come out of the corridors without getting robbed!");
        }
        setNewCR(); // Sets new crossroads to the player and prints the corresponding information.
    }

    /**
     * If player has any cash he can get robbed. If he has any cash he gets robbed in 1/3 of times.
     * @return True if player got robbed. False if not.
     */
    private boolean gotRobbed(){
        if(cash > 0){
            Random rand = new Random();
            int robbed = rand.nextInt(3);
            if(robbed == 1){
                cash = 0;
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return Returns randomly chosen integer 0-4 inclusive.
     */
    private int produceRandX(){
        Random rand = new Random();
        return rand.nextInt(5);
    }

    /**
     *
     * @return Returns randomly chosen integer 0-5 inclusive.
     */
    private int produceRandY(){
        Random rand = new Random();
        return rand.nextInt(6);
    }

    /**
     * Asks the user to type "Y" or "N" to confirm their action. The input is not case-sensitive.
     * @return Answer string which is either "y" or "n". Note the lower case of the return strings.
     */
    private boolean getAnswer(String message){
        JLabel label = new JLabel(message);
        label.setFont(new Font("Ariel", Font.BOLD, 15));
        if (JOptionPane.showConfirmDialog(null, label, "Confirm selection",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            return true;
        }
        return false;
    }

    public int getBankBalance(){
        return bankBalance;
    }

    public int getCash(){
        return cash;
    }

    private void showMessage(String message){
        JLabel label = new JLabel(message);
        label.setFont(new Font("Ariel", Font.BOLD, 15));
        JOptionPane.showMessageDialog(null, label, "!!!", JOptionPane.OK_OPTION);
    }

    private void showNiceMessage(String message){
        JLabel label = new JLabel(message);
        label.setFont(new Font("Ariel", Font.BOLD, 15));
        JOptionPane.showMessageDialog(null, label, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
