public class QuantityMeasurementApp {

    // ✅ UC8: Standalone-style enum with FULL responsibility
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        // ✅ Convert TO base unit (feet)
        public double convertToBase(double value) {
            return value * toFeetFactor;
        }

        // ✅ Convert FROM base unit (feet)
        public double convertFromBase(double baseValue) {
            return baseValue / toFeetFactor;
        }
    }

    // ✅ Quantity Class (simplified → UC8)
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

        // ✅ UC5: Convert instance
        public double convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double base = unit.convertToBase(value);
            return targetUnit.convertFromBase(base);
        }

        // ✅ UC5: Static convert
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            if (source == null || target == null) {
                throw new IllegalArgumentException("Units cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }

            double base = source.convertToBase(value);
            return target.convertFromBase(base);
        }

        // ✅ UC3: Equality
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity)) return false;

            Quantity other = (Quantity) obj;

            double thisBase = this.unit.convertToBase(this.value);
            double otherBase = other.unit.convertToBase(other.value);

            return Double.compare(thisBase, otherBase) == 0;
        }

        // ✅ UC6: Add → result in THIS unit
        public Quantity add(Quantity other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            double sumBase =
                    this.unit.convertToBase(this.value) +
                            other.unit.convertToBase(other.value);

            double result = this.unit.convertFromBase(sumBase);

            return new Quantity(result, this.unit);
        }

        // ✅ UC7: Add with TARGET unit
        public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Quantities cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double sumBase =
                    q1.unit.convertToBase(q1.value) +
                            q2.unit.convertToBase(q2.value);

            double result = targetUnit.convertFromBase(sumBase);

            return new Quantity(result, targetUnit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ✅ MAIN METHOD
    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        // UC3 Equality
        System.out.println("Equal: " + q1.equals(q2));

        // UC5 Conversion
        System.out.println("1 foot → inches: " +
                Quantity.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES));

        // UC6 Addition
        System.out.println("UC6 (Feet): " + q1.add(q2));

        // UC7 Addition with target
        System.out.println("UC7 (Inches): " +
                Quantity.add(q1, q2, LengthUnit.INCHES));

        System.out.println("UC7 (Yards): " +
                Quantity.add(q1, q2, LengthUnit.YARDS));
    }
}