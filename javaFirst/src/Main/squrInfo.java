package Main;

//:):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):)
public class squrInfo {// this class contains all information needed about all the squares on the table
    public boolean red = false;
    public boolean blue = false;
    public boolean king = false;
    public boolean highlight = false;
    public boolean possibleMove = false;
    public int width, height;
    public int locationX, locationY;

    public boolean oppositeColor(String color) {
        return (this.blue && color.equals("red")) || (this.red && color.equals("blue"));
    }
}