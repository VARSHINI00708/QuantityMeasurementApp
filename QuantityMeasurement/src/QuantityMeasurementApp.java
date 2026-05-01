public class QuantityMeasurementApp {

    // -------------------------
    // INTERFACE (UC10)
    // -------------------------
    interface IMeasurable {
        double getConversionFactor();
        double convertToBaseUnit(double value);
        double convertFromBaseUnit(double baseValue);
        String getUnitName();
    }

    // -------------------------
    // LENGTH UNIT
    // -------------------------
    enum LengthUnit implements IMeasurable {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double factor;

        LengthUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public double convertToBaseUnit(double value) {
            return value * factor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // -------------------------
    // WEIGHT UNIT
    // -------------------------
    enum WeightUnit implements IMeasurable {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double factor;

        WeightUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public double convertToBaseUnit(double value) {
            return value * factor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // -------------------------
    // VOLUME UNIT (UC11)
    // -------------------------
    enum VolumeUnit implements IMeasurable {
        LITRE(1.0),
        MILLILITRE(0.001),
        GALLON(3.78541);

        private final double factor;

        VolumeUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public double convertToBaseUnit(double value) {
            return value * factor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // -------------------------
    // GENERIC QUANTITY (UC10)
    // -------------------------
    static class Quantity<U extends IMeasurable> {

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (unit == null)
                throw new IllegalArgumentException("Unit cannot be null");

            if (!Double.isFinite(value))
                throw new IllegalArgumentException("Invalid value");

            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public U getUnit() {
            return unit;
        }

        public Quantity<U> convertTo(U targetUnit) {
            double base = unit.convertToBaseUnit(value);
            double result = targetUnit.convertFromBaseUnit(base);
            return new Quantity<>(result, targetUnit);
        }

        public Quantity<U> add(Quantity<U> other) {
            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);
            double sum = base1 + base2;
            double result = unit.convertFromBaseUnit(sum);
            return new Quantity<>(result, unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);
            double sum = base1 + base2;
            double result = targetUnit.convertFromBaseUnit(sum);
            return new Quantity<>(result, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity<?> other)) return false;

            if (!this.unit.getClass().equals(other.unit.getClass()))
                return false;

            double v1 = unit.convertToBaseUnit(value);
            double v2 = ((IMeasurable) other.unit).convertToBaseUnit(other.value);

            return Math.abs(v1 - v2) < 0.0001;
        }
    }

    // -------------------------
    // MAIN METHOD (DEMO)
    // -------------------------
    public static void main(String[] args) {

        // LENGTH
        var l1 = new Quantity<>(1.0, LengthUnit.FEET);
        var l2 = new Quantity<>(12.0, LengthUnit.INCHES);
        System.out.println(l1.equals(l2)); // true

        // WEIGHT
        var w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        var w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        System.out.println(w1.equals(w2)); // true

        // VOLUME
        var v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        var v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        System.out.println(v1.equals(v2)); // true
    }
}