public class QuantityMeasurementApp {

    // ENUM for units
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

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

        // STATIC conversion API (UC5)
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

        // ✅ UC6 ADD METHOD (CORRECT PLACE)
        public Quantity add(Quantity other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            double thisInFeet = this.unit.toFeet(this.value);
            double otherInFeet = other.unit.toFeet(other.value);

            double sumInFeet = thisInFeet + otherInFeet;

            double resultValue = this.unit.fromFeet(sumInFeet);

            return new Quantity(resultValue, this.unit);
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

    // ✅ SINGLE MAIN METHOD
    public static void main(String[] args) {

        // UC5 demo
        System.out.println("Feet → Inches: " +
                Quantity.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES));

        // UC6 demo
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        Quantity result = q1.add(q2);

        System.out.println("Addition Result: " + result); // 2.0 FEET
    }
}