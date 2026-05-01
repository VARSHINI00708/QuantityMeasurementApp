public class QuantityMeasurementApp {

    // ============================
    // UC10: COMMON INTERFACE
    // ============================
    interface IMeasurable {
        double getConversionFactor();
        double convertToBaseUnit(double value);
        double convertFromBaseUnit(double baseValue);
        String getUnitName();
    }

    // ============================
    // LENGTH UNIT (UC8 + UC10)
    // ============================
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

    // ============================
    // WEIGHT UNIT (UC9 + UC10)
    // ============================
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

    // ============================
    // GENERIC QUANTITY CLASS (UC10)
    // ============================
    static class Quantity<U extends IMeasurable> {

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid value");
            }
            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public U getUnit() {
            return unit;
        }

        // ============================
        // CONVERSION
        // ============================
        public Quantity<U> convertTo(U targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double base = unit.convertToBaseUnit(value);
            double converted = targetUnit.convertFromBaseUnit(base);

            return new Quantity<>(round(converted), targetUnit);
        }

        // ============================
        // ADD (UC6)
        // ============================
        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        // ============================
        // ADD WITH TARGET (UC7)
        // ============================
        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            if (other == null) {
                throw new IllegalArgumentException("Other cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double thisBase = unit.convertToBaseUnit(value);
            double otherBase = other.unit.convertToBaseUnit(other.value);

            double sum = thisBase + otherBase;
            double result = targetUnit.convertFromBaseUnit(sum);

            return new Quantity<>(round(result), targetUnit);
        }

        // ============================
        // EQUALITY
        // ============================
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity<?> other = (Quantity<?>) obj;

            // Prevent cross-category comparison
            if (!this.unit.getClass().equals(other.unit.getClass())) {
                return false;
            }

            double thisBase = unit.convertToBaseUnit(value);
            double otherBase = other.unit.convertToBaseUnit(other.value);

            return Double.compare(thisBase, otherBase) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(unit.convertToBaseUnit(value));
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }

        private double round(double value) {
            return Math.round(value * 100.0) / 100.0;
        }
    }

    // ============================
    // MAIN METHOD (DEMO)
    // ============================
    public static void main(String[] args) {

        // ===== LENGTH =====
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCHES);

        System.out.println("Length Equality: " + l1.equals(l2));
        System.out.println("Length Convert: " + l1.convertTo(LengthUnit.INCHES));
        System.out.println("Length Add (FEET): " + l1.add(l2, LengthUnit.FEET));
        System.out.println("Length Add (YARDS): " + l1.add(l2, LengthUnit.YARDS));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equality: " + w1.equals(w2));
        System.out.println("Weight Convert: " + w1.convertTo(WeightUnit.GRAM));
        System.out.println("Weight Add (KG): " + w1.add(w2, WeightUnit.KILOGRAM));

        // ===== CROSS CATEGORY (SAFE) =====
        System.out.println("Cross Category Equality: " + l1.equals(w1)); // false
    }
}