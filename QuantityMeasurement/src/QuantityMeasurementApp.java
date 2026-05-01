public class QuantityMeasurementApp {

    // ================= INTERFACE =================
    interface IMeasurable {
        double getConversionFactor();

        default double convertToBaseUnit(double value) {
            return value * getConversionFactor();
        }

        default double convertFromBaseUnit(double baseValue) {
            return baseValue / getConversionFactor();
        }

        String getUnitName();
    }

    // ================= VOLUME UNIT =================
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

        public String getUnitName() {
            return name();
        }
    }

    // ================= WEIGHT UNIT =================
    enum WeightUnit implements IMeasurable {
        KILOGRAM(1.0),
        GRAM(0.001);

        private final double factor;

        WeightUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // ================= LENGTH UNIT =================
    enum LengthUnit implements IMeasurable {
        FEET(1.0),
        INCHES(1.0 / 12.0);

        private final double factor;

        LengthUnit(double factor) {
            this.factor = factor;
        }

        public double getConversionFactor() {
            return factor;
        }

        public String getUnitName() {
            return name();
        }
    }

    // ================= GENERIC QUANTITY =================
    static class Quantity<U extends IMeasurable> {

        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            if (unit == null)
                throw new IllegalArgumentException("Unit cannot be null");

            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public U getUnit() {
            return unit;
        }

        private void validate(Quantity<U> other) {
            if (other == null)
                throw new IllegalArgumentException("Quantity cannot be null");

            if (!this.unit.getClass().equals(other.unit.getClass()))
                throw new IllegalArgumentException("Different measurement categories");
        }

        // ===== EQUALS =====
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity<?> other)) return false;

            if (!this.unit.getClass().equals(other.unit.getClass()))
                return false;

            double base1 = unit.convertToBaseUnit(value);
            double base2 = ((IMeasurable) other.unit).convertToBaseUnit(other.value);

            return Math.abs(base1 - base2) < 0.0001;
        }

        // ===== CONVERT =====
        public Quantity<U> convertTo(U targetUnit) {
            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit null");

            double base = unit.convertToBaseUnit(value);
            double result = targetUnit.convertFromBaseUnit(base);

            return new Quantity<>(round(result), targetUnit);
        }

        // ===== ADD =====
        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            validate(other);

            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);

            double resultBase = base1 + base2;
            double result = targetUnit.convertFromBaseUnit(resultBase);

            return new Quantity<>(round(result), targetUnit);
        }

        // ===== SUBTRACT (UC12) =====
        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
            validate(other);

            if (targetUnit == null)
                throw new IllegalArgumentException("Target unit null");

            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);

            double resultBase = base1 - base2;
            double result = targetUnit.convertFromBaseUnit(resultBase);

            return new Quantity<>(round(result), targetUnit);
        }

        // ===== DIVIDE (UC12) =====
        public double divide(Quantity<U> other) {
            validate(other);

            double base1 = unit.convertToBaseUnit(value);
            double base2 = other.unit.convertToBaseUnit(other.value);

            if (base2 == 0)
                throw new ArithmeticException("Division by zero");

            return base1 / base2;
        }

        // ===== ROUND =====
        private double round(double val) {
            return Math.round(val * 100.0) / 100.0;
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }
    }

    // ================= MAIN METHOD =================
    public static void main(String[] args) {

        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);

        System.out.println("=== ADD ===");
        System.out.println(v1.add(v2)); // 5.5 L

        System.out.println("=== SUBTRACT ===");
        System.out.println(v1.subtract(v2)); // 4.5 L

        System.out.println("=== DIVIDE ===");
        System.out.println(v1.divide(v2)); // 10

        System.out.println("=== CONVERT ===");
        System.out.println(v1.convertTo(VolumeUnit.MILLILITRE)); // 5000 mL

        System.out.println("=== EQUALS ===");
        System.out.println(v1.equals(new Quantity<>(5000, VolumeUnit.MILLILITRE))); // true
    }
}