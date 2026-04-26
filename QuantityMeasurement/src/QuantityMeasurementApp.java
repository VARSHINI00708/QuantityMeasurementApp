public class QuantityMeasurementApp {

    // ENUM for units
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084); // 1 cm = 0.0328084 feet

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
    }

    // Quantity Class
    static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }
            this.value = value;
            this.unit = unit;
        }

        // Convert to another unit
        public double convertTo(LengthUnit targetUnit) {
            double valueInFeet = unit.toFeet(this.value);
            return targetUnit.fromFeet(valueInFeet);
        }

        // STATIC conversion API (UC5 requirement)
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }

            double valueInFeet = source.toFeet(value);
            return target.fromFeet(valueInFeet);
        }

        // Equality check
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;

            double thisInFeet = this.unit.toFeet(this.value);
            double otherInFeet = other.unit.toFeet(other.value);

            return Double.compare(thisInFeet, otherInFeet) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        System.out.println("Feet → Inches: " +
                Quantity.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES));

        System.out.println("Yards → Feet: " +
                Quantity.convert(3.0, LengthUnit.YARDS, LengthUnit.FEET));

        System.out.println("CM → Inches: " +
                Quantity.convert(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
    }
}