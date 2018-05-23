import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 *
 * This class creates the main window of the game and allows player to click buttons to move the chars around.
 *
 * @author Luka Kralj
 * @version January 2018
 */
public class MainWindow implements ActionListener{

    private HashMap<String, JLabel> allMapLabels;
    private JLabel cashLabel;
    private JLabel bankLabel;
    private JLabel playerItemsLabel;
    private JLabel crossroadsItemsLabel;
    private static JLabel warnings;
    private static JPanel paneWarnings;
    private Player player;
    private JFrame mainWindow;
    private JButton take;
    private JButton drop;
    private JButton withdraw;
    private JButton deposit;
    private String selected;

    public MainWindow(String name){
        this.player = new Player(name, 1, 1, this);

        allMapLabels = new HashMap<>();
        fillAllMapLabels();

        mainWindow = new JFrame("Game CityDelivery");
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel helpButton = createHelpButton();
        mainWindow.add(helpButton);

        JPanel mapTitle = new JPanel(new GridLayout(1,2));
        mapTitle.setPreferredSize(new Dimension(mainWindow.getWidth(), 50));
        Label mapLabel = new Label("Map of the city:");
        mapLabel.setFont(new Font("Ariel", Font.BOLD, 15));
        mapLabel.setAlignment(Label.CENTER);
        mapTitle.add(mapLabel);
        mapTitle.add(new JLabel());
        mainWindow.add(mapTitle);

        JPanel map = createMap();
        mainWindow.add(map);

        JPanel mapSpacing = new JPanel();
        mapSpacing.setPreferredSize(new Dimension(mainWindow.getWidth(), 20));
        mainWindow.add(mapSpacing);

        JPanel balanceLabel = new JPanel(new GridLayout(1, 2));
        balanceLabel.add(new JLabel());
        Label balance = new Label("Your current balance is:");
        balance.setAlignment(Label.CENTER);
        balance.setFont(new Font("Arial", Font.PLAIN, 15));
        balanceLabel.add(balance);
        mainWindow.add(balanceLabel);

        JPanel balanceLabels = createBalanceLabels();
        balanceLabels.setPreferredSize(new Dimension(mainWindow.getWidth(), 50));
        mainWindow.add(balanceLabels);



        GridLayout grid = new GridLayout(1, 4);
        JPanel paneItems = new JPanel(grid);
        paneItems.setPreferredSize(new Dimension(mainWindow.getWidth(), 100));
        playerItemsLabel = new JLabel("");
        playerItemsLabel.setFont(new Font("Arial", Font.PLAIN, 15));;
        playerItemsLabel.setVerticalAlignment(SwingConstants.TOP);
        playerItemsLabel.setVerticalTextPosition(SwingConstants.TOP);
        paneItems.add(playerItemsLabel);
        crossroadsItemsLabel = new JLabel("");
        crossroadsItemsLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        crossroadsItemsLabel.setVerticalAlignment(SwingConstants.TOP);
        crossroadsItemsLabel.setVerticalTextPosition(SwingConstants.TOP);
        paneItems.add(crossroadsItemsLabel);
        paneItems.add(new JLabel());
        paneItems.add(new JLabel());
        mainWindow.add(paneItems);


        JPanel takeDrop = createTakeDrop();
        mainWindow.add(takeDrop);
        JPanel takeDropSpacing = new JPanel();
        takeDropSpacing.setPreferredSize(new Dimension(mainWindow.getWidth(), 20));
        mainWindow.add(takeDropSpacing);

        paneWarnings = new JPanel(new GridLayout(1,1));
        warnings = new JLabel();
        warnings.setFont(new Font("Arial", Font.BOLD, 17));
        warnings.setVerticalTextPosition(SwingConstants.TOP);
        paneWarnings.setMinimumSize(new Dimension(mainWindow.getWidth(), 25));
        //paneWarnings.setPreferredSize(new Dimension(mainWindow.getWidth(), 25));
        paneWarnings.add(warnings);
        mainWindow.add(paneWarnings);

        JPanel warningSpacing = new JPanel();
        warningSpacing.setPreferredSize(new Dimension(mainWindow.getWidth(), 20));
        mainWindow.add(warningSpacing);

        JPanel moveButtons = createMoveButtons();
        mainWindow.add(moveButtons);

        JPanel copyright = new JPanel(new GridLayout(1,3));
        copyright.setPreferredSize(new Dimension(mainWindow.getWidth(), 20));
        copyright.add(new JLabel());

        JButton copyButton = new JButton("\u00a9 Luka Kralj, 2018");
        copyButton.setForeground(Color.GRAY);
        copyButton.setContentAreaFilled(false);
        copyButton.setBorderPainted(false);
        copyButton.setActionCommand("copyright");
        copyButton.addActionListener(this);
        copyright.add(copyButton);

        copyright.add(new JLabel());
        mainWindow.add(copyright);


        updateBankLabel();
        updateCashLabel();
        updateCrossroadsItemsLabel();
        updatePlayerItemsLabel();
        updateTakeDrop();
        updateMap();

        mainWindow.setResizable(false);
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    /**
     * Fills the hash map with pairs of locations and appropriate JLabels.
     * String is created as: "" + row + col
     * Method sets the necessary properties of each label before adding it to the hash map.
     */
    private void fillAllMapLabels(){
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 11; col++){
                int status = Characters.getStatus(row, col);
                JLabel label = new JLabel();
                label.setOpaque(true);
                if(status == -1 || status == 2){
                    label.setBackground(Color.BLACK);
                }
                else if((row % 2 == 0 && col % 2 == 1 && status == 0) ||
                        (row % 2 == 1 && col % 2 == 0 && status == 0)) {
                    ImageIcon icon = new ImageIcon(getClass().getResource("cross.jpg"));
                    label.setIcon(icon);
                }
                else if(status == 1 || status == 0){
                    label.setBackground(Color.WHITE);
                }
                else{
                    label.setBackground(Color.WHITE);
                }
                label.setMinimumSize(new Dimension(50, 50));
                label.setPreferredSize(new Dimension(50, 50));
                label.setMaximumSize(new Dimension(50, 50));
                allMapLabels.put("" + row + col, label);
            }
        }
    }

    public void updateMap(){
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 11; col++){
                int status = Characters.getStatus(row, col);
                String s = "" + row + col;
                JLabel toUpdate = allMapLabels.get(s);
                toUpdate.setText("");

                if(status == -1 || status == 2 ||
                        (row % 2 == 0 && col % 2 == 1 && status == 0) ||
                        (row % 2 == 1 && col % 2 == 0 && status == 0)){
                    continue;
                }
                else if(status == 1 || status == 0){
                    toUpdate.setBackground(Color.WHITE);
                    toUpdate.setIcon(null);
                }
                else if (status == 3){ // Player.
                    ImageIcon icon = new ImageIcon(getClass().getResource("player.jpg"));
                    toUpdate.setIcon(icon);
                }
                else if (status == 4){ // Workers.
                    ImageIcon icon = new ImageIcon(getClass().getResource("workers.jpg"));
                    toUpdate.setIcon(icon);
                }
                else if (status == 5){ // Circus.
                    ImageIcon icon = new ImageIcon(getClass().getResource("circus.jpg"));
                    toUpdate.setIcon(icon);
                }
            }
        }
    }

    /**
     * Adds all labels to the grid creating a map.
     */
    private void initialiseGrid(JPanel wnd){
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 11; col++){
                String s = "" + row + col;
                wnd.add(allMapLabels.get(s));
            }
        }
    }

    /**
     * Creates a map to be added to main window.
     * @return
     */
    private JPanel createMap(){
        GridLayout gridBoth = new GridLayout(1,2);
        JPanel both = new JPanel(gridBoth);

        GridLayout grid = new GridLayout(9, 11);
        JPanel map = new JPanel(grid);
        initialiseGrid(map);
        both.add(map);

        ImageIcon icon = new ImageIcon(getClass().getResource("map_finished.jpg"));
        both.add(new JLabel(icon));

        return both;
    }

    /**
     * Creates buttons to move the player.
     * TODO:
     *  - assign actions to the buttons
     * @return
     */
    private JPanel createMoveButtons(){
        GridLayout grid = new GridLayout(1, 8);
        JPanel moveButtons = new JPanel(grid);

        JButton up = new JButton("UP");
        up.setFont(new Font("Arial", Font.BOLD, 13));
        up.setActionCommand("up");
        up.addActionListener(this);
        JButton down = new JButton("DOWN");
        down.setFont(new Font("Arial", Font.BOLD, 13));
        down.setActionCommand("down");
        down.addActionListener(this);
        JButton left = new JButton("LEFT");
        left.setFont(new Font("Arial", Font.BOLD, 13));
        left.setActionCommand("left");
        left.addActionListener(this);
        JButton right = new JButton("RIGHT");
        right.setFont(new Font("Arial", Font.BOLD, 13));
        right.setActionCommand("right");
        right.addActionListener(this);

        moveButtons.add(up);
        moveButtons.add(down);
        moveButtons.add(left);
        moveButtons.add(right);

        moveButtons.add(new JLabel());
        moveButtons.add(new JLabel());
        moveButtons.add(new JLabel());

        JButton exit = new JButton("EXIT");
        exit.setFont(new Font("Arial", Font.BOLD, 13));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        moveButtons.add(exit);

        return moveButtons;
    }

    private JPanel createHelpButton(){
        GridLayout grid = new GridLayout(1, 8);

        JPanel helpButton = new JPanel(grid);

        JButton help = new JButton("HELP");
        help.setFont(new Font("Arial", Font.BOLD, 13));
        help.setActionCommand("help");
        help.addActionListener(this);
        helpButton.add(help);
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        helpButton.add(new JLabel());
        return helpButton;
    }

    private JPanel createTakeDrop(){
        GridLayout grid = new GridLayout(1, 8);
        JPanel takeDrop = new JPanel(grid);

        drop = new JButton("DROP ITEM");
        drop.setFont(new Font("Arial", Font.BOLD, 13));
        drop.setActionCommand("drop");
        drop.addActionListener(this);
        takeDrop.add(drop);
        takeDrop.add(new JLabel());

        take = new JButton("PICK UP ITEM");
        take.setFont(new Font("Arial", Font.BOLD, 13));
        take.setActionCommand("take");
        take.addActionListener(this);
        takeDrop.add(take);
        takeDrop.add(new JLabel());


        withdraw = new JButton("WITHDRAW");
        withdraw.setFont(new Font("Arial", Font.BOLD, 13));
        withdraw.setActionCommand("withdraw");
        withdraw.addActionListener(this);
        takeDrop.add(new JLabel());
        takeDrop.add(withdraw);

        deposit = new JButton("DEPOSIT");
        deposit.setFont(new Font("Arial", Font.BOLD, 13));
        deposit.setActionCommand("deposit");
        deposit.addActionListener(this);
        takeDrop.add(deposit);
        takeDrop.add(new JLabel());

        return takeDrop;
    }

    public void actionPerformed(ActionEvent e){
        String action = e.getActionCommand();
        if (action.equals("up")){
            player.goUp();
        }
        else if (action.equals("down")){
            player.goDown();
        }
        else if (action.equals("left")){
            player.goLeft();
        }
        else if (action.equals("right")){
            player.goRight();
        }
        else if (action.equals("help")){
            helpWindow();
        }
        else if (action.equals("take")){
            takeItem();
        }
        else if (action.equals("drop")){
            dropItem();
        }
        else if (action.equals("withdraw")){
            withdrawCash();
        }
        else if (action.equals("deposit")){
            depositCash();
        }
        else if (action.equals("copyright")){
            copyright();
        }
        updateMap();
        updatePlayerItemsLabel();
        updateCrossroadsItemsLabel();
        updateTakeDrop();
        // We need to update cash and bank balance labels in case the player got robbed underground.
        updateCashLabel();
        updateBankLabel();

    }

    /**
     * Creates labels to display information about the money.
     * @return
     */
    private JPanel createBalanceLabels(){
        GridLayout grid = new GridLayout(1, 4);
        JPanel both = new JPanel(grid);
        both.setPreferredSize(new Dimension(mainWindow.getWidth(), 50));

        JLabel current = new JLabel("Your current items are:");
        current.setFont(new Font("Arial", Font.BOLD, 15));
        JLabel currentCR = new JLabel("Items at this crossroads are:");
        currentCR.setFont(new Font("Arial", Font.BOLD, 15));
        both.add(current);
        both.add(currentCR);


        JPanel paneCash = new JPanel(new GridLayout(1,4));
        paneCash.add(new JLabel());
        paneCash.add(new JLabel());
        JLabel cash = new JLabel("Cash:");
        cash.setFont(new Font("Arial", Font.BOLD, 15));
        paneCash.add(cash);
        cashLabel = new JLabel("");
        cashLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        paneCash.add(cashLabel);

        both.add(paneCash);

        JPanel paneBank = new JPanel(new GridLayout(1,4));
        JLabel bank = new JLabel("Bank:");
        bank.setFont(new Font("Arial", Font.BOLD, 15));
        paneBank.add(bank);
        bankLabel = new JLabel("");
        bankLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        paneBank.add(bankLabel);
        paneBank.add(new JLabel());
        paneBank.add(new JLabel());

        both.add(paneBank);

        return both;
    }

    /**
     * Update cash label to display current amount of money in cash.
     */
    private void updateCashLabel(){
        cashLabel.setText("" + player.getCash());
    }

    /**
     * Update cash label to display current amount of money in bank.
     */
    private void updateBankLabel(){
        bankLabel.setText("" + player.getBankBalance());
    }

    /**
     * Updates the label to show updated list of player's items.
     */
    private void updatePlayerItemsLabel(){
        String text = "<html>";
        if (player.getItems().size() == 0){
            text = "You have no items.";
        }
        else {
            for(Item i : player.getItems()){
                text = text + " - " + i.getName() + "<br>";
            }
            text = text + "</html>";
        }
        playerItemsLabel.setText(text);
    }

    /**
     * Updates the label to show updated list of items at current crossroads.
     */
    private void updateCrossroadsItemsLabel(){
        String text = "<html>";
        if (player.getCurrentCR() == null || player.getCurrentCR().getItems().size() == 0){
            text = "No items available here.";
        }
        else {
            for(Item i : player.getCurrentCR().getItems()){
                text = text + " - " + i.getName() + "<br>";
            }
            text = text + "</html>";
        }
        crossroadsItemsLabel.setText(text);
    }

    public static void updateWarnings(String text){
        if (text == null){
            warnings.setText("");
            paneWarnings.setOpaque(false);
            paneWarnings.setBorder(null);
            paneWarnings.repaint();
        }
        else {
            warnings.setText(text);
            paneWarnings.setOpaque(true);
            paneWarnings.setBackground(Color.WHITE);
            paneWarnings.setBorder(BorderFactory.createLineBorder(Color.RED));
            paneWarnings.repaint();
        }
    }

    private void updateTakeDrop(){
        if (player.getItems().size() == 0 || player.getCurrentCR() == null){
            drop.setEnabled(false);
            drop.setVisible(false);
        }
        else {
            drop.setEnabled(true);
            drop.setVisible(true);
        }

        if (player.getCurrentCR() == null || player.getCurrentCR().getItems().size() == 0){
            take.setEnabled(false);
            take.setVisible(false);
        }
        else {
            take.setEnabled(true);
            take.setVisible(true);
        }

        if (player.getCurrentCR() != null && player.getCurrentCR().getCRName().equals("ATM")){
            deposit.setEnabled(true);
            withdraw.setEnabled(true);
            deposit.setVisible(true);
            withdraw.setVisible(true);
        }
        else {
            deposit.setEnabled(false);
            withdraw.setEnabled(false);
            deposit.setVisible(false);
            withdraw.setVisible(false);
        }
    }

    private void helpWindow(){
        String text = "<html>You are a delivery guy and your job is to deliver three parcels to the customers.<br>" +
                "You can walk around the city but you can only stop at the crossroads, because all the<br>" +
                "institutions have entrances on the corners of the buildings.<br>" +
                "If you end your day with enough money for a 20-pound dinner, you win!<br><br>" +
                "<b>INSTRUCTIONS:</b><br>" +
                "<ul> - Firstly, buy a parcel from the Delivery Shop (pay in cash). Once you deliver the parcel the customer<br>" +
                "   will repay you and give you a tip as well. (Note: order of deliveries is not important.)</ul>" +
                "<ul> - If there is a special crossroads where you currently are you will be able to either pick up<br>" +
                "   or buy certain items or leave them there to pick them up later.</ul>" +
                "<ul> - Once you buy something you can leave it wherever you want and pick it up later without paying again.</ul>" +
                "<ul> - Plan your route! If you make too many moves your car might need repairs.</ul>" +
                "<ul> - Beware of the corners you cannot access! There is a reason those paths are closed. People have gone<br>" +
                "   missing after entering those areas.</ul><br>" +
                "<b>The Underground Corridor System:</b><br>" +
                " The city has a very old underground corridor system which was used for faster travelling from one end<br>" +
                " of the city to another (hint: this will only count as one move regardless of the distance travelled).<br>" +
                " Unfortunately, this system hasn't been well maintained and now has some restrictions:<br>" +
                "<ul> - The system only has one entrance but you can exit on whichever crossroads.</ul>" +
                "<ul> - The entrance is only accessible from the upper side through one of the big buildings (hint: there are only three<br>" +
                "   such crossroads that could allow that, so it shouldn't be too hard to find).</ul>" +
                "<ul> - The system is frequently flooded so there will only be one exit when you enter (N.B. you might end up in a dangerous area!).</ul>" +
                "<ul> - This place is very convenient for thieves. However, they will only take the cash (but not every time).</ul><br>" +
                "<b><i>Best of luck!</i></b><br></html>";
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 15));
        JOptionPane.showMessageDialog(null, label, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void takeItem(){
        ArrayList<Item> items = player.getCurrentCR().getItems();

        JPanel layout = new JPanel(new GridLayout(2, 1));

        JLabel info = new JLabel("Which item do you want to pick up?");
        info.setFont(new Font("Ariel", Font.BOLD, 15));
        layout.add(info);

        HashMap<String, Item> itm = new HashMap<>();
        String[] itemNames = new String[items.size()];
        for(int i = 0; i < items.size(); i++){
            itm.put(items.get(i).getName(), items.get(i));
            itemNames[i] = items.get(i).getName();
        }



        JComboBox<String> itemList = new JComboBox<>(itemNames);
        itemList.setSelectedIndex(-1);
        itemList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = itemList.getSelectedItem().toString();
            }
        });

        layout.add(itemList);

        JOptionPane.showMessageDialog(null, layout, "Pick up item", JOptionPane.QUESTION_MESSAGE);
        if (selected != null){
            player.takeItem(itm.get(selected));
            selected = null;
        }
    }

    private void dropItem(){
        ArrayList<Item> items = player.getItems();

        JPanel layout = new JPanel(new GridLayout(2, 1));

        JLabel info = new JLabel("Which item do you want to drop?");
        info.setFont(new Font("Ariel", Font.BOLD, 15));
        layout.add(info);

        HashMap<String, Item> itm = new HashMap<>();
        String[] itemNames = new String[items.size()];
        for(int i = 0; i < items.size(); i++){
            itm.put(items.get(i).getName(), items.get(i));
            itemNames[i] = items.get(i).getName();
        }



        JComboBox<String> itemList = new JComboBox<>(itemNames);
        itemList.setSelectedIndex(-1);
        itemList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = itemList.getSelectedItem().toString();
            }
        });

        layout.add(itemList);

        JOptionPane.showMessageDialog(null, layout, "Drop item", JOptionPane.QUESTION_MESSAGE);
        if (selected != null){
            player.dropItem(itm.get(selected));
            selected = null;
        }
    }

    private void withdrawCash(){
        boolean success = false;
        JLabel label = new JLabel("How much cash would you like to withdraw?");
        label.setFont(new Font("Ariel", Font.BOLD, 15));


        while (!success){
            String input = JOptionPane.showInputDialog(null, label,
                    "Withdraw", JOptionPane.QUESTION_MESSAGE);

            if (input == null || input.equals("")) {
                return;
            }

            try {
                int amount = Integer.parseInt(input);

                if (amount > player.getBankBalance()) {
                    label.setText("<html>How much cash would you like to withdraw?<br><br>" +
                            "Sorry! You don't have that much money in bank!</html>");
                }
                else if (amount <= 0) {
                    label.setText("<html>How much cash would you like to withdraw?<br><br>" +
                            "Please enter a positive whole-number amount!</html>");
                }
                else {
                    player.takeCash(amount);
                    success = true;
                }
            }
            catch (NumberFormatException e) {
                label.setText("<html>How much cash would you like to withdraw?<br><br>" +
                        "Please enter a whole-number amount!</html>");
            }
        }
    }

    private void depositCash(){
        boolean success = false;
        JLabel label = new JLabel("How much cash would you like to deposit?");
        label.setFont(new Font("Ariel", Font.BOLD, 15));


        while (!success){
            String input = JOptionPane.showInputDialog(null, label,
                    "Deposit", JOptionPane.QUESTION_MESSAGE);

            if (input == null || input.equals("")) {
                return;
            }

            try {
                int amount = Integer.parseInt(input);

                if (amount > player.getCash()) {
                    label.setText("<html>How much cash would you like to deposit?<br><br>" +
                            "Sorry! You don't have that much money in cash!</html>");
                }
                else if (amount <= 0) {
                    label.setText("<html>How much cash would you like to deposit?<br><br>" +
                            "Please enter a positive whole-number amount!</html>");
                }
                else {
                    player.dropCash(amount);
                    success = true;
                }
            }
            catch (NumberFormatException e) {
                label.setText("<html>How much cash would you like to deposit?<br><br>" +
                        "Please enter a whole-number amount!</html>");
            }
        }
    }

    private void copyright(){
        String text = "<html>Icons:<br>" +
                "<ul> - <i>Circus icon:</i> Clipartbarn.com. (2016). <i>\"Tent clip art images free clipart 7.\"</i><br>" +
                "Retrieved 2 January, 2018, from http://clipartbarn.com/tent-clipart_19161/.</ul>" +
                "<ul> - <i>Construction icon:</i> Retrieved 2 January, 2018, from http://www.galmarini.com/.</ul>" +
                "<ul> - <i>Delivery icon</i> Flaticon.com.(2013-2018). <i>\"Logistics delivery truck in movement free icon.\"</i><br>" +
                "Retrieved 2 January, 2018, from https://www.flaticon.com/free-icon/logistics-delivery-truck-in-movement_46046.</ul>" +
                "These icons were modified (resized) in MS Paint.<br><br>" +
                "All other icons were created by myself in MS Paint.<br><br>" +
                "This application is an upgrade of the text-based game CityDelivery which was made as<br>" +
                "an assignment for Programming Practice And Applications, a first-year module of the programme<br>" +
                "MSci Computer Science at King's College London.<br><br><br>" +
                "<i>\u00a9 Luka Kralj, 2018</i><br></html>";

        JLabel label = new JLabel(text);
        label.setFont(new Font("Ariel", Font.PLAIN, 15));
        JOptionPane.showMessageDialog(null, label, "Copyright", JOptionPane.INFORMATION_MESSAGE);
    }
}
