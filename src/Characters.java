import java.util.Set;
import java.util.HashMap;

/**
 * This class is part of CityDelivery application.
 * CityDelivery is a text based game. The aim is to complete three deliveries.
 *
 * This class allows creation of characters and placing them on the map of a city.
 * It provides methods to move characters around the city via open paths.
 * After each move the character is ends up one of the neighbouring crossroads to its previous position.
 *
 * @author Luka Kralj
 * @version December 2017
 */
public class Characters{
    //This array holds the values which mean different things:
    // - 0 in the intersection of even row and even column means free crossroads
    // - all other 0s represent a closed path
    // - -1 means that there is a building on that position
    // - 1 represents an open path
    // - 2 represents a building and the entrance into The Underground Corridor System.
    private static int[][] map = {
            { 0,  1, 0,  1, 0,  1, 0,  0, 0,  0, 0},
            { 1, -1, 1, -1, 1, -1, 0, -1, 0, -1, 0},
            { 0,  1, 0,  1, 0,  1, 0,  1, 0,  1, 0},
            { 1, -1, 1, -1, 2, -1, 1, -1, 1, -1, 1},
            { 0, -1, 0,  1, 0,  1, 0,  1, 0,  1, 0},
            { 1, -1, 1, -1, 1, -1,-1, -1, 1, -1, 0},
            { 0,  1, 0, -1, 0,  1, 0,  1, 0,  1, 0},
            { 0, -1, 1, -1, 1, -1, 1, -1,-1, -1, 1},
            { 0,  0, 0,  1, 0,  1, 0,  1, 0,  1, 0},
    };

    //This variable stores unique character ID number for easier display on map.
    //It runs from 3 onwards because values up to 2 already have their assigned meanings on the map.
    private static int nextId = 3;
    //Stores pairs of ID numbers and names of characters.
    private static HashMap<Integer, String> allChar = new HashMap<>();

    //Name of the character.
    private String name;
    //Row in which the character is (from top to bottom).
    private int currentX;
    //Column in whish the character is (from left to right).
    private int currentY;
    //Stores the character ID number.
    private int charId;

    /**
     * Create new character and place it on the map.
     *
     * @param name Name of the character.
     * @param x A "user-friendly" coordinate: valid values are 0-4 inclusive.
     *          Row in which we want the character to be (counting from top to bottom).
     * @param y A "user-friendly" coordinate: valid values are 0-5 inclusive.
     *          Column in which we want the character to be (counting from left to right).
     */
    public Characters(String name, int x, int y){
        this.name = name;
        currentX = x*2;
        currentY = y*2;
        charId = nextId;
        map[currentX][currentY] = charId;
        allChar.put(charId, name);
        nextId++; //increase unique id number
    }

    /**
     * This method prints the current map of the city and the positions of the characters.
     * If called before creating any characters it will display an empty map of the city.
     */
    public static void printMap(){
        System.out.println("This is a map of the city. The numbers on the edges represent the coordinates.");
        System.out.println("Note:");
        System.out.println(" - symbols \\ and / represent corners of the city");
        System.out.println(" - [] represents a building");
        System.out.println(" - x represents a closed path");
        printAllChar(); // list all characters in the game
        System.out.println();
        Crossroads.printAllCR(); // lists all the "special" crossroads
        System.out.println();
        System.out.println("(X\\Y) 0   1   2   3   4   5"); // Y-coordinates for user navigation
        for(int row = 0; row < 9; row++){
            if(row%2==0){
                System.out.print("  " + row/2 + " "); // X-coordinates for user navigation
            }
            else{
                System.out.print("    ");
            }
            if(row == 0) System.out.print("/");
            else if(row == 8) System.out.print("\\");
            else System.out.print(" ");

            for(int col = 0; col < 11; col++){
                //Control variable that gives information whether empty space should be printed.
                boolean isEmpty = true;
                //Checks if it is a building.
                if(map[row][col]==-1 || map[row][col]==2){
                    System.out.print("[]");
                    isEmpty = false;
                }
                //Checks if it is a closed path.
                else if(row%2==0 && col%2==1 && map[row][col]==0 ||
                row%2==1 && col%2==0 && map[row][col]==0){
                    System.out.print(" x");
                    isEmpty = false;
                }
                else{
                    //Checks if any of the charactes is in that place.
                    //Number 3 is set here becasue it is the lowest possible ID number.
                    for(int id = 3; id-3 < allChar.size(); id++){
                        if(map[row][col]==id){
                            System.out.print(" " + id);
                            isEmpty = false;
                        }
                    }
                }
                if(isEmpty){
                    //Prints empty space to keep the map aligned.
                    System.out.print("  ");
                }
            }

            if(row == 8) System.out.print("/");
            if(row == 0) System.out.print("\\");
            System.out.println();
        }
    }

    /**
     * This method prints all the characters and their ID numbers as a map note.
     */
    private static void printAllChar(){
        Set<Integer> allId = allChar.keySet();
        for(int id : allId){
            System.out.println(" - " + id + " represents \"" + allChar.get(id) + "\" on the map");
        }
    }

    public static int getStatus(int x, int y){
        return map[x][y];
    }

    /**
     * If the character can move up (according to the map) this method will change the character's position.
     * @return Integer that gives information whether the move was successful or not. If it was not it gives
     *          a unique integer for each of the possible "faults" in moving.
     */
    public int goUp(){
        try{
            //Checks if the chosen path is a building or underground. The underground is only approachable from upside.
            if(map[currentX-1][currentY]==-1 || map[currentX-1][currentY]==2){
                return -1;
            }
            //Checks if the path is closed.
            else if(map[currentX-1][currentY]==0){
                return 0;
            }
            //Checks if the next crossroads is empty.
            else if(map[currentX-2][currentY]!=0){
                return 3;
            } 
            else{
                //If we reach this the move is allowed.

                //Characters can stop at crossroads thus we need to increment by 2.
                //Increment by 1 would mean placing the character on the path.
                map[currentX][currentY] = 0;
                currentX -= 2;
                map[currentX][currentY] = charId;
                return 4;
            }
        }
        catch(IndexOutOfBoundsException e){
            //If we reach here the player attempted to leave the city.
            return 1;
        }
    }

    /**
     * If the character can move down (according to the map) this method will change the character's position.
     * @return Integer that gives information whether the move was successful or not. If it was not it gives
     *          a unique integer for each of the possible "faults" in moving.
     */
    public int goDown(){
        try{
            //Checks if the chosen path is a building.
            if(map[currentX+1][currentY]==-1){
                return -1;
            }
            //Checks if the path is closed.
            else if(map[currentX+1][currentY]==0){
                return 0;
            }
            //Checks if the path is an entrance to the underground.
            else if(map[currentX+1][currentY]==2){
                return 2;
            }
            //Checks if the next crossroads is empty.
            else if(map[currentX+2][currentY]!=0){
                return 3;
            } 
            else{
                //If we reach this the move is allowed.

                //Characters can stop at crossroads thus we need to increment by 2.
                //Increment by 1 would mean placing the character on the path.
                map[currentX][currentY] = 0;
                currentX += 2;
                map[currentX][currentY] = charId;
                return 4;
            }
        }
        catch(IndexOutOfBoundsException e){
            //If we reach here the player attempted to leave the city.
            return 1;
        }
    }

    /**
     * If the character can move left (according to the map) this method will change the character's position.
     * @return Integer that gives information whether the move was successful or not. If it was not it gives
     *          a unique integer for each of the possible "faults" in moving.
     */
    public int goLeft(){
        try{
            //There is no need to check for an underground (...==2) because this situation is not possible.

            //Checks if the chosen path is building.
            if(map[currentX][currentY-1]==-1){
                return -1;
            }
            //Checks if the path is closed.
            else if(map[currentX][currentY-1]==0){
                return 0;
            }
            //Checks if the next crossroads is empty.
            else if(map[currentX][currentY-2]!=0){
                return 3;
            } 
            else{
                //If we reach this the move is allowed.

                //Characters can stop at crossroads thus we need to increment by 2.
                //Increment by 1 would mean placing the character on the path.
                map[currentX][currentY] = 0;
                currentY -= 2;
                map[currentX][currentY] = charId;
                return 4;
            }
        }
        catch(IndexOutOfBoundsException e){
            //If we reach here the player attempted to leave the city.
            return 1;
        }
    }

    /**
     * If the character can move right (according to the map) this method will change the character's position.
     * @return Integer that gives information whether the move was successful or not. If it was not it gives
     *          a unique integer for each of the possible "faults" in moving.
     */
    public int goRight(){
        try{
            //There is no need to check for an underground (...==2) because this situation is not possible.

            //Checks if the chosen path is building or underground.
            if(map[currentX][currentY+1]==-1){
                return -1;
            }
            //Checks if the path is closed.
            else if(map[currentX][currentY+1]==0){
                return 0;
            }
            //Checks if the next crossroads is empty. If it isn't then it informs the player.
            else if(map[currentX][currentY+2]!=0){
                return 3;
            } 
            else{
                //If we reach this the move is allowed.

                //Characters can stop at crossroads thus we need to increment by 2.
                //Increment by 1 would mean placing the character on the path.
                map[currentX][currentY] = 0;
                currentY += 2;
                map[currentX][currentY] = charId;
                return 4;
            }
        }
        catch(IndexOutOfBoundsException e){
            //If we reach here the player attempted to leave the city.
            return 1;
        }
    }

    /**
     * Returns the name of the character.
     */
    public String getCharName(){
        return name;
    }

    /**
     *
     * Returns the row in which the character is (from top to bottom).
     * @return DOES NOT return a "user-friendly" coordinate! Return values are 0-8 inclusive.
     */
    public int getCurrentX(){
        return currentX;
    }

    /**
     * Returns the column in which the character is (from left to right).
     * @return DOES NOT return a "user-friendly" coordinate! Return values are 0-10 inclusive.
     */
    public int getCurrentY(){
        return currentY;
    }

    /**
     * This method is used when randomly changing the player character after entering The Underground
     * Corridor System.
     * @param curX Current player's position x: a "user-friendly" coordinate: valid values are 0-4 inclusive.
     * @param curY Current player's position y: a "user-friendly" coordinate: valid values are 0-5 inclusive.
     * @param randX New randomly chosen position x: a "user-friendly" coordinate: valid values are 0-4 inclusive.
     * @param randY New randomly chosen position y: a "user-friendly" coordinate: valid values are 0-5 inclusive.
     * @return True if changing the player's position was successful, false if not.
     */
    public boolean tryChangeCR(int curX, int curY, int randX, int randY){
        // If the new crossroads is not empty we cannot go there.
        if(map[randX*2][randY*2] != 0){
            return false;
        }
        map[curX*2][curY*2] = 0;
        // I used number three explicitly as this method is only used for changing the player's position.
        map[randX*2][randY*2] = 3;
        currentX = randX*2;
        currentY = randY*2;
        return true;
    }
}
