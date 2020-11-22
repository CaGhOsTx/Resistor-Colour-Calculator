import java.security.InvalidParameterException;

import custom.structures.Table;

public class Resistor { //a class responsible for all of the logic behind what a resistor is
    private final Table colourTable;
    private final short size;
    private final Colour[] colours;
    public double resistance, minResistance, maxResistance;
    private char unit;

    Resistor(short size, String[] colourNames) { //a constructor that basically runs all the methods properly and allows it to be very encapsulated
        colourTable = new Table("data\\data");
        sizeAndColourCheck(size, colourNames);
        this.size = size;
        colours = new Colour[size];
        getColourData(colourNames);
        colourOrderMismatchCheck();
        calculateResistance();
        calculateMaxResistance();
        calculateMinResistance();
    }

   public String print(double n) {
        return String.format("%.3g", convertUnits(n)) + " " + unit + "Ω";
   }
   public String printResistance() {
        return String.format("%.3g", convertUnits(resistance)) + " ± " + colours[colours.length - 1].tolerance + "% " + unit + "Ω";
   }

    private void sizeAndColourCheck(short size, String[] colourNames) {
        if (invalidSize(size))
            throw new InvalidParameterException("Invalid size");
        if(invalidColour(colourNames))
            throw new InvalidParameterException("Invalid colours");
    }

    private boolean invalidSize(short size) {
        return size != 4 && size != 5;
    }

    private boolean invalidColour(String[] colourNames) {
        int count = 0;
        String[] columns = colourTable.getColumns();
        for (String name : colourNames) {
            for(String column : columns) {
                if(column.equalsIgnoreCase(name)) {
                    count++;
                    break;
                }
            }
        }
        return count != 4 && count != 5;
    }

    private void colourOrderMismatchCheck() { //this is ugly I know :/
        if(size == 4) {
            if (colours[0].value == -1f)
                throw new InvalidParameterException(colours[0].name + " has no digit value");
            else if (colours[1].value == -1f)
                throw new InvalidParameterException(colours[1].name + " has no digit value");
            else if (colours[2].multiplier == -1f)
                throw new InvalidParameterException(colours[2].name + " has no multiplier value");
            else if (colours[3].tolerance == -1f)
                throw new InvalidParameterException(colours[3].name + "  has no tolerance value");
        }
        else {
            if (colours[0].value == -1f)
                throw new InvalidParameterException(colours[0].name + " has no digit value");
            else if (colours[1].value == -1f)
                throw new InvalidParameterException(colours[1].name + " has no digit value");
            else if (colours[2].value == -1f)
                throw new InvalidParameterException(colours[2].name + " has no digit value");
            else if (colours[3].multiplier == -1f)
                throw new InvalidParameterException(colours[3].name + " has no multiplier value");
            else if (colours[4].tolerance == -1f)
                throw new InvalidParameterException(colours[4].name + "  has no tolerance value");
        }
    }


    private void getColourData(String[] colourNames) {
        for(int i = 0; i < colours.length; i++) {
            float[] data = Table.convert(colourTable.extractColumn(colourNames[i]));
            colours[i] = new Colour(data[0], data[1], data[2], colourNames[i]);
        }
    }

    private void calculateResistance() {
        float number, multiplier;
        if (size == 4) {
            number = colours[0].value * 10 + colours[1].value;
            multiplier = colours[2].multiplier;
        } else {
            number = colours[0].value * 100 + colours[1].value * 10 + colours[2].value;
            multiplier = colours[3].multiplier;
        }
        resistance = number * multiplier;
    }

    private void calculateMaxResistance() {
        maxResistance = resistance + (resistance * colours[colours.length - 1].tolerance / 100);
    }

    private void calculateMinResistance() {
        minResistance = resistance - resistance * (colours[colours.length - 1].tolerance / 100);
    }

    private double convertUnits(double n) { //converts the units to to manageable sizes by using the unit prefixes
        char[] units = {'G', 'M', 'k', 'm', 'μ', 'n'};
        int i = 0;
        if(n >= 1 && n < 1000) {
            unit = '\0';
            return n;
        }
        for(double factor = 1_000_000_000; factor > 0.000000001; factor /= 1000, i++) {
            if(factor == 1) i--;
            if (n > factor && factor != 1){
                unit =  units[i];
                return  n / factor;
            }
        }
        throw new IllegalArgumentException();
    }

    private static class Colour { //a sub class made so that the methods are more intuitive, the table data could be used straight away
        String name;                //but using this class makes it more intuitive.
        float value, multiplier, tolerance;

        Colour(float value, float multiplier, float tolerance, String name){
            this.value = value;
            this.multiplier = multiplier;
            this.tolerance = tolerance;
            this.name = name;
        }

    }
}
