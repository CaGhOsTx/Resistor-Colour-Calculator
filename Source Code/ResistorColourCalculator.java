import processing.core.PApplet;
import processing.core.PImage;

/*
*   Carlos Milkovic
*   10.11.2020
*/

import java.security.InvalidParameterException;

public class ResistorColourCalculator extends PApplet {
    short size;
    private PImage image;
    private boolean sizeIsSet, sliderIsOpen, calcPressed;
    Box size4, size5, sizeTitle, calculate;
    BigBox sizeSelector, colourMenu, colourSlider, calcSection, resistor, colourMenuText;

    //these variables are required here in order to be able to pass them into different run methods, since main is not the only one.

    public void settings() {
        size(750, 550);
    }

    public void setup() {
        image = loadImage("resistor.png");
        textFont(createFont("Roboto.ttf", 10));
        loadSizeSelector(); // all of these methods with load in the name initialise the boxes. They only exist to clean up the code a little.

    }

    public void draw() {
        clear();
        background(242, 214, 194);
        image.resize(375, 50);
        image(image, 35, 60);
        if (size4.mouseWithinBox())
            sizeSelector.displayAllAndReplaceHover(size4);
        else if (size5.mouseWithinBox())
            sizeSelector.displayAllAndReplaceHover(size5);
        else
            sizeSelector.displayAll();
        if (sizeIsSet) {
            resistor.displayInnerBoxes();
            colourMenu.checkForMouseHoverAndDisplay();
            colourMenuText.displayInnerBoxes();
        }
        if (sliderIsOpen) {
            colourSlider.checkForMouseHoverAndDisplay();
        } else if (calculate != null) { //sometimes calculate does not initialise after mouseClick on the colourSlider,
            if (calculate.mouseWithinBox()) //it is not a big deal and hardly ever noticeable so this just avoids the NullPointer crash.
                calculate.displayHover();
            else
                calculate.display();
        }
        if (calcPressed)
            calcSection.displayAll();
    }

    public void mouseClicked() {
        calcPressed = false;
        if (size4.mouseWithinBox()) { //this if chain just sets the colour of the Select size buttons and assigns,
            size4.boxColour = Colour.RED; // some booleans so the next part of the program gets initialised and displayed.
            size4.textColour = Colour.WHITE;
            size5.boxColour = Colour.WHITE;
            size5.textColour = Colour.BLACK;
            colourMenu = new BigBox(450, 170, 290, 365, 0, loadColourMenu(4));
            colourMenuText = new BigBox(530, 240, 180, 220, 0, loadColourMenuTextBoxes(4));
            size = 4;
            sizeIsSet = true;
            sliderIsOpen = false; //this is the colourSlider initializer.
        } else if (size5.mouseWithinBox()) {
            size5.boxColour = Colour.RED;
            size5.textColour = Colour.WHITE;
            size4.boxColour = Colour.WHITE;
            size4.textColour = Colour.BLACK;
            colourMenu = new BigBox(450, 170, 290, 365, 0, loadColourMenu(5));
            colourMenuText = new BigBox(530, 240, 180, 280, 0, loadColourMenuTextBoxes(5));
            size = 5;
            sizeIsSet = true;
            sliderIsOpen = false;
        }
        if (sizeIsSet && !sliderIsOpen) {  //initialises the calculate "box" and sets the calcSection boolean to true so that the BigBox initialises.
            calculate = new Box(125, 302.5f, 200, 100, 2);
            calculate.setText("Calculate", 34);
            if (calculate.mouseWithinBox())
                calcPressed = true;
        }
        if (sliderIsOpen) {
            calculate = null; //removes the calculate box to prevent initialising the calcSection when clicking on colours on the colour slider,
            for (Box sliderColourBox : colourSlider.getBoxes()) {  //which sometimes overlap the calculate box.
                if (sliderColourBox.mouseWithinBox()) {
                    for (Box menuColourBox : colourMenu.getBoxes()) {
                        if (menuColourBox.y == sliderColourBox.y) {  // if the y coordinates of the colour on the menu and slider match,
                            menuColourBox.boxColour = sliderColourBox.boxColour; // set the menu colour to the slider colour picked.
                            sliderIsOpen = false;  //once a colour is picked, close the slider.
                        }
                    }
                }
            }
        }
        if (sizeIsSet && !sliderIsOpen && calculate == null) { // initialises the calculate box. after the sliderBox is closed again.
            calculate = new Box(125, 302.5f, 200, 100, 2);
            calculate.setText("Calculate", 34);
        }
        if (sizeIsSet) { // initialises the colourMenu BigBox (idk why I called it BigBox, should've named it container or something but it's funny xD).
            for (Box box : colourMenu.getBoxes()) { //if a menuColourBox is clicked on open the slider
                if (box.mouseWithinBox() && isNotTheFirstBox(box)) {
                    colourSlider = new BigBox(15, box.y - 10, 720, 60, 2, loadSliderBoxes(-35, 10));
                    sliderIsOpen = true;
                    break;
                } else {
                    colourSlider = null;
                    sliderIsOpen = false;
                }
                resistor = new BigBox(185, 80, 50, 200, 0, loadResistor()); //this is the little coloured stripes on the resistor image.
            }
            if (calcPressed) //if calculate is clicked, initialise the calcSection bigBox (this one is the most interesting :D).
                calcSection = new BigBox(10, 170, 430, 365, 0, loadCalcSection());
        }
    }

    public static void main(String[] args) {
        PApplet.main("ResistorColourCalculator");
    }

    public boolean isNotTheFirstBox(Box box) {
        return !box.equals(colourMenu.getBoxes()[0]);
    }

    public void hoverFill(Colour c) { // its method that basically reduces the brightness of the colours, I could've added more colour's to the enum,
        int[] rgb = c.rgb(); //but it was easier to just add a few more methods to the box and BigBox classes, otherwise I would've had to add,
        fill(rgb[0] - 50, rgb[1] - 50, rgb[2] - 50); // exceptions to some loops in load<whateverBox> methods.
    }

    public void fill(Colour c) { //overloaded Processing method to use the enum constants, easier to type Colour.ORANGE then it is to memorise colour values.
        int[] rgb = c.rgb();
        fill(rgb[0], rgb[1], rgb[2]);
    }

    public Box[] loadResistor() { //this the resistor colour bands loader
        int length = colourMenu.boxes.length - 1;
        Box[] colours = new Box[length];
        int n;
        if (size == 4)
            n = 20;
        else n = 15;
        for (int i = 0; i < length; i++)
            colours[i] = new Box(3.5f + i * n, -15, 9.5f, 40, 0);
        colours[length - 1].h = 48.5f;
        colours[length - 1].y = -19;
        for (int i = 0; i < length; i++) //since I've discovered forEach loops, I hate to use these(forEach loops look so nice), but they're more versatile.
            colours[i].boxColour = colourMenu.boxes[i + 1].boxColour; // applies the same colours as the menu colours.
        return colours;
    }

    public Box[] loadCalcSection() {
        int length = colourMenu.boxes.length;
        String[] colourNames = new String[length];
        for (int i = 1; i < length; i++)
            colourNames[i - 1] = String.valueOf(colourMenu.boxes[i].boxColour);
        try {    //tries to do this, but the resistor is set up so it checks for colour errors and throws an exception
            var resistor = new Resistor(size, colourNames);
            var resistance = new Box(205, 0, 100, 100, 0);
            resistance.setText("Resistance: " + resistor.printResistance(), 25);
            var minResistance = new Box(205, 50, 100, 100, 0);
            minResistance.setText("Min resistance: " + resistor.print(resistor.minResistance), 25);
            var maxResistance = new Box(205, 100, 100, 100, 0);
            maxResistance.setText("Max resistance: " + resistor.print(resistor.maxResistance), 25);
            return new Box[]{resistance, minResistance, maxResistance};  // creates the boxes that display the results in the calcSection BigBox.
        } catch (InvalidParameterException e) {  //catches the exception and displays it in the calc section
            var error = new Box(240, 20, 100, 100, 0);
            error.setText("Error: " + e.getMessage(), 24);
            error.textColour = Colour.RED;
            return new Box[]{error};
        }
    }

    public void loadSizeSelector() { //all of these loaders do the same, the ones from here have loops because their boxes have a lot in common
        sizeTitle = new Box(45, 10, 200, 70, 0);
        sizeTitle.setText("Select size", 37);
        size4 = new Box(10, 80, 120, 50, 2);
        size4.setText("4", 32);
        size5 = new Box(160, 80, 120, 50, 2);
        size5.setText("5", 32);
        sizeSelector = new BigBox(450, 10, 290, 150, 0, sizeTitle, size4, size5);
    }

    public Box[] loadSliderBoxes(float x, float y) {
        int i = 0;
        Box[] boxes = new Box[Colour.values().length];
        for (Colour colour : Colour.values()) {
            boxes[i++] = new Box(x + i * 50, y, 40, 40, 2, colour);
        }
        return boxes;
    }

    public Box[] loadColourMenuTextBoxes(int size) {
        int n = 1;
        Box[] boxes = new Box[size];
        for (int i = 0; i < size; i++) {
            boxes[i] = new Box(75, -2f + i * 60, 40, 40, 0);
            boxes[i].setText("Colour " + n++, 28);
        }
        return boxes;
    }

    public Box[] loadColourMenu(int size) {
        Box[] boxes = new Box[size + 1];
        boxes[0] = new Box(50, 0, 200, 70, 0);
        boxes[0].setText("Select colours", 32);
        for (int i = 1; i < boxes.length; i++)
            boxes[i] = new Box(55, 70 + (i - 1) * 60, 40, 40, 2, Colour.values()[i]);
        return boxes;
    }

    class BigBox extends Box { //this is a big box which contains smaller boxes, very useful to move a whole section of "boxes" with 2 x,y changes preserving structure
        private Box shadow; //shadow box, I tried to make a shadow :P
        private final Box[] boxes; //since adding boxes to the BigBoxes would result in array size change, it is better to just reconstruct a new box every time.

        //I could've added a check in the mouseWithinBox method to see if the box is null, but
        BigBox(float x, float y, float w, float h, float strokeWeight, Box... littleBoxes) {
            super(x, y, w, h, strokeWeight);
            boxes = littleBoxes;
            shadow = new Box(x + 2.5f, y + 2.5f, w, h, 0, Colour.GREY);
            group();
        }

        private void setPosition(float x, float y) { //this method is a way to automatically move all of the boxes within a BigBox when its position changes
            this.x = x;
            this.y = y;
            group();
        }

        public Box[] getBoxes() {
            return boxes;
        }

        public void setShadow(float xOffset, float yOffset, Colour c) {  //ended up not using this, but I kept it anyway.
            shadow = new Box(x + xOffset, y + yOffset, w, h, 0, c);
        }

        public void checkForMouseHoverAndDisplay() {
            for (Box box : boxes) {
                if (box.mouseWithinBox()) {
                    displayAllAndReplaceHover(box);
                    return;
                }
            }
            displayAll();
        }

        public void display() {  // displays the boxes, this was just a convenient format to throw in the draw method.
            shadow.display();
            strokeWeight(strokeWeight);
            fill(boxColour);
            rect(x, y, w, h, 7);
        }

        public void displayInnerBoxes() { //displays the boxes it contains without displaying the BigBox, this was used with the resistor colour bands.
            for (Box box : boxes)
                box.display();
        }

        public void displayInnerBoxesHover(Box box) { //displays the box at which the mouse pointer is darker.
            for (Box box1 : boxes) {
                if (box1.equals(box)) {
                    box1.displayHover();
                } else box1.display();
            }
        }

        public void displayAll() {
            display();
            displayInnerBoxes();
        }

        public void displayAllAndReplaceHover(Box box) {
            display();
            displayInnerBoxesHover(box);
        }

        public void group() { //way easier to position things relative to something
            for (Box box : boxes) {
                box.x += this.x;
                box.y += this.y;
            }
        }
    }

    class Box {

        protected float x, y, w, h;
        protected float textSize, strokeWeight;
        protected float xTextOffset, yTextOffset;
        protected String text;
        protected Colour textColour, boxColour;

        //bunch of constructors for different use case scenarios
        Box(float x, float y, float w, float h, float strokeWeight) {
            setDimensions(x, y, w, h);
            this.strokeWeight = strokeWeight;
            boxColour = Colour.WHITE;
        }

        Box(float x, float y, float w, float h, float strokeWeight, Colour boxColour) {
            setDimensions(x, y, w, h);
            this.strokeWeight = strokeWeight;
            this.boxColour = boxColour;
        }

        private void setDimensions(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public void setText(String text, int textSize, Colour textColour) { //sets the text of the box
            this.text = text;
            this.textSize = textSize;
            this.textColour = textColour;
            xTextOffset = w / 2 - text.length() * 7.666f;
            yTextOffset = h / 2 + textSize / 2.6666f;
        }

        public void setText(String text, int textSize) {
            setText(text, textSize, Colour.BLACK);
        }

        public boolean mouseWithinBox() { //checks whether the mouse is within the box
            return (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h);
        }

        public void displayHover() { //displays the box with a darker colour
            if (textColour == null) {
                displayEmptyHover();
                return;
            }
            stroke(0, 0, 0);
            strokeWeight(strokeWeight);
            hoverFill(boxColour);
            if (strokeWeight != 0 && text != null) {
                rect(x, y, w, h, 7);
            }
            fill(textColour);
            textSize(textSize);
            text(text, x + xTextOffset, y + yTextOffset);
        }

        public void display() { //displays the box
            if (textColour == null) {
                displayEmpty();
                return;
            }
            stroke(0, 0, 0);
            strokeWeight(strokeWeight);
            fill(boxColour);
            if (strokeWeight != 0 && text != null) {
                rect(x, y, w, h, 7);
            }
            fill(textColour);
            textSize(textSize);
            text(text, x + xTextOffset, y + yTextOffset);
        }

        private void displayEmpty() { //displays a box without text
            if (strokeWeight == 0)
                noStroke();
            else
                stroke(0, 0, 0);
            strokeWeight(strokeWeight);
            fill(boxColour);
            rect(x, y, w, h, 7);
        }

        private void displayEmptyHover() { //displays a box without text with a shaded colour because of the mouse hovering over it
            if (strokeWeight == 0)
                noStroke();
            else
                stroke(0, 0, 0);
            strokeWeight(strokeWeight);
            hoverFill(boxColour);
            rect(x, y, w, h, 7);
        }
    }
}
