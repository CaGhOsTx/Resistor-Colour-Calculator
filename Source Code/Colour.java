public enum Colour { //simple enum to store colour values (it was easier to write Colour.BLACK than putting in the rgb values)

    BLACK(10,10,10),
    BROWN(102, 51, 0),
    RED(255,0,0),
    ORANGE(255, 102, 0),
    YELLOW(255, 255, 0),
    GREEN(0, 153, 0),
    BLUE(0,102,153),
    VIOLET(153, 0, 255),
    GREY(128,128,128),
    WHITE(255,255,250),
    GOLD(255,215,0),
    SILVER(211,211,211),
    PINK(255,192,203),
    NONE(217,187,122);

    private final int r,g,b;

     Colour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int[] rgb () {
        return new int[] {r, g, b};
    }

}
