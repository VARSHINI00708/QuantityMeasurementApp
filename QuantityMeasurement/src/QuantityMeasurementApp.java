public class QuantityMeasurementApp {

    // =======================
    // LENGTH UNIT (UC8)
    // =======================
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double convertToBase(double value) {
            return value * toFeetFactor;
        }

        public double convertFromBase(double baseValue) {
            return baseValue / toFeetFactor;
        }
    }

    // =======================
    // LENGTH QUANTITY
    // =======================
    static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");

            this.value = value;
            this.unit = unit;
        }

        // Convert
        public Quantity convertTo(LengthUnit targetUnit) {
            double base = unit.convertToBase(value);
            double result = targetUnit.convertFromBase(base);
            return new Quantity(result, targetUnit);
        }

        // Static convert
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            double base = source.convertToBase(value);
            return target.convertFromBase(base);
        }

        // Equality
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity)) return false;

            Quantity other = (Quantity) obj;

            double thisBase = this.unit.convertToBase(this.value);
            double otherBase = other.unit.convertToBase(other.value);

            return Double.compare(thisBase, otherBase) == 0;
        }

        // UC6 Add
        public Quantity add(Quantity other) {
            double sumBase =
                    this.unit.convertToBase(this.value) +
                            other.unit.convertToBase(other.value);

            double result = this.unit.convertFromBase(sumBase);

            return new Quantity(result, this.unit);
        }

        // UC7 Add with target
        public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
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

    // =======================
    // WEIGHT UNIT (UC9)
    // =======================
    enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double toKgFactor;

        WeightUnit(double toKgFactor) {
            this.toKgFactor = toKgFactor;
        }

        public double convertToBase(double value) {
            return value * toKgFactor;
        }

        public double convertFromBase(double baseValue) {
            return baseValue / toKgFactor;
        }
    }

    // =======================
    // WEIGHT QUANTITY
    // =======================
    static class QuantityWeight {
        private final double value;
        private final WeightUnit unit;

        public QuantityWeight(double value, WeightUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");

            this.value = value;
            this.unit = unit;
        }

        // Convert
        public QuantityWeight convertTo(WeightUnit targetUnit) {
            double base = unit.convertToBase(value);
            double result = targetUnit.convertFromBase(base);
            return new QuantityWeight(result, targetUnit);
        }

        // Static convert
        public static double convert(double value, WeightUnit source, WeightUnit target) {
            double base = source.convertToBase(value);
            return target.convertFromBase(base);
        }

        // Equality
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof QuantityWeight)) return false;

            QuantityWeight other = (QuantityWeight) obj;

            double thisBase = this.unit.convertToBase(this.value);
            double otherBase = other.unit.convertToBase(other.value);

            return Double.compare(thisBase, otherBase) == 0;
        }

        // UC6 Add
        public QuantityWeight add(QuantityWeight other) {
            double sumBase =
                    this.unit.convertToBase(this.value) +
                            other.unit.convertToBase(other.value);

            double result = this.unit.convertFromBase(sumBase);

            return new QuantityWeight(result, this.unit);
        }

        // UC7 Add with target
        public static QuantityWeight add(QuantityWeight q1, QuantityWeight q2, WeightUnit targetUnit) {
            double sumBase =
                    q1.unit.convertToBase(q1.value) +
                            q2.unit.convertToBase(q2.value);

            double result = targetUnit.convertFromBase(sumBase);

            return new QuantityWeight(result, targetUnit);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // =======================
    // MAIN METHOD
    // =======================
    public static void main(String[] args) {

        // ===== LENGTH =====
        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCHES);

        System.out.println("Length Equal: " + q1.equals(q2));
        System.out.println("Length Convert: " +
                Quantity.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES));
        System.out.println("Length Add (Feet): " + q1.add(q2));
        System.out.println("Length Add (Yards): " +
                Quantity.add(q1, q2, LengthUnit.YARDS));

        // ===== WEIGHT =====
        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        System.out.println("Weight Equal: " + w1.equals(w2));
        System.out.println("Weight Convert: " +
                QuantityWeight.convert(1.0, WeightUnit.KILOGRAM, WeightUnit.GRAM));
        System.out.println("Weight Add (kg): " + w1.add(w2));
        System.out.println("Weight Add (pound): " +
                QuantityWeight.add(w1, w2, WeightUnit.POUND));
    }
}