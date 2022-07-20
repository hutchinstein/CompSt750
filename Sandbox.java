import java.util.Random;
import java.util.ArrayList;

public class Sandbox{
    public static int generateCoord(){
        Random random = new Random();
        int coordinate = random.nextInt(16);
        return coordinate;
    }

    public static void displayNeighbors(int row, int col){
        for(int row_neighbor = (row - 1); row_neighbor < (row + 2); row_neighbor++){
            for(int col_neighbor = (col - 1); col_neighbor < (col + 2); col_neighbor++){
                System.out.println("Checking " + row_neighbor + "," + col_neighbor);
            } 
        }
    }
    public static void main(String[] args) {
        ArrayList<Integer> coords = new ArrayList<Integer>();
        coords.add(1);
        coords.add(2);
        coords.add(3);
        coords.add(4);
        coords.add(7);
        coords.add(6);
        /*int last_x_coord = 0; 
        System.out.println(coords.size());
        while (last_x_coord < coords.size() - 1 ){
            System.out.println(coords.get(last_x_coord));
            System.out.println(coords.get(last_x_coord + 1));
            System.out.println("___");
            last_x_coord += 2;
            System.out.println(last_x_coord);
        }
        System.out.println(coords.size());
        for(int i : coords){
            System.out.println(i);
        }*/
        System.out.println(coords);
        coords.remove(7);
    }
}