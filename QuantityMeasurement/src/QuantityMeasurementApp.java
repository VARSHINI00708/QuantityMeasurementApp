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

        // Convert instance
        public double convertTo(LengthUnit targetUnit) {
            double valueInFeet = unit.toFeet(this.value);
            return targetUnit.fromFeet(valueInFeet);
        }

        // Static convert (UC5)
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

        // Equality (UC3/UC4)
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity other = (Quantity) obj;

            double thisFeet = this.unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            return Double.compare(thisFeet, otherFeet) == 0;
        }

        // UC6: Add → first unit
        public Quantity add(Quantity other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            double thisFeet = this.unit.toFeet(this.value);
            double otherFeet = other.unit.toFeet(other.value);

            double sumFeet = thisFeet + otherFeet;

            double resultValue = this.unit.fromFeet(sumFeet);

            return new Quantity(resultValue, this.unit);
        }

        // UC7: Add with target unit
        public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Quantities cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double q1Feet = q1.unit.toFeet(q1.value);
            double q2Feet = q2.unit.toFeet(q2.value);

            double sumFeet = q1Feet + q2Feet;

            double resultValue = targetUnit.fromFeet(sumFeet);

            return new Quantity(resultValue, targetUnit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        // UC6
        System.out.println("UC6: " + q1.add(q2));

        // UC7
        System.out.println("UC7 (INCHES): " +
                Quantity.add(q1, q2, LengthUnit.INCHES));

        System.out.println("UC7 (YARDS): " +
                Quantity.add(q1, q2, LengthUnit.YARDS));
    }
}