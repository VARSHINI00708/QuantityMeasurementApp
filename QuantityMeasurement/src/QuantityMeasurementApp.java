import java.util.function.DoubleBinaryOperator;

// ================= INTERFACE =================
interface IMeasurable {
    double toBase(double value);
    double fromBase(double baseValue);
}

// ================= LENGTH =================
enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCHES(1.0 / 12.0);

    private final double toFeet;

    LengthUnit(double toFeet) {
        this.toFeet = toFeet;
    }

    public double toBase(double value) {
        return value * toFeet;
    }

    public double fromBase(double baseValue) {
        return baseValue / toFeet;
    }
}

// ================= WEIGHT =================
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(1.0 / 1000.0);

    private final double toKg;

    WeightUnit(double toKg) {
        this.toKg = toKg;
    }

    public double toBase(double value) {
        return value * toKg;
    }

    public double fromBase(double baseValue) {
        return baseValue / toKg;
    }
}

// ================= VOLUME =================
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(1.0 / 1000.0);

    private final double toLitre;

    VolumeUnit(double toLitre) {
        this.toLitre = toLitre;
    }

    public double toBase(double value) {
        return value * toLitre;
    }

    public double fromBase(double baseValue) {
        return baseValue / toLitre;
    }
}

// ================= QUANTITY CLASS =================
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    // ================= ENUM =================
    private enum Operation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Divide by zero");
            return a / b;
        });

        private final DoubleBinaryOperator op;

        Operation(DoubleBinaryOperator op) {
            this.op = op;
        }

        double apply(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }

    // ================= VALIDATION =================
    private void validate(Quantity<U> other, U targetUnit, boolean checkTarget) {
        if (other == null) throw new IllegalArgumentException("Other cannot be null");
        if (other.unit == null) throw new IllegalArgumentException("Invalid unit");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different measurement categories");

        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException("Invalid value");

        if (checkTarget && targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
    }

    // ================= CORE HELPER =================
    private double perform(Quantity<U> other, Operation op) {
        double base1 = unit.toBase(value);
        double base2 = other.unit.toBase(other.value);
        return op.apply(base1, base2);
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    // ================= ADD =================
    public Quantity<U> add(Quantity<U> other) {
        return add(other, unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit, true);
        double baseResult = perform(other, Operation.ADD);
        double result = targetUnit.fromBase(baseResult);
        return new Quantity<>(round(result), targetUnit);
    }

    // ================= SUBTRACT =================
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit, true);
        double baseResult = perform(other, Operation.SUBTRACT);
        double result = targetUnit.fromBase(baseResult);
        return new Quantity<>(round(result), targetUnit);
    }

    // ================= DIVIDE =================
    public double divide(Quantity<U> other) {
        validate(other, null, false);
        return perform(other, Operation.DIVIDE);
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }
}

// ================= MAIN APP =================
public class QuantityMeasurementApp {
    public static void main(String[] args) {

        // ===== SUBTRACTION =====
        Quantity<LengthUnit> q1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6, LengthUnit.INCHES);

        System.out.println("Subtract: " + q1.subtract(q2));
        System.out.println("Subtract (in inches): " + q1.subtract(q2, LengthUnit.INCHES));

        // ===== DIVISION =====
        Quantity<LengthUnit> q3 = new Quantity<>(24, LengthUnit.INCHES);
        Quantity<LengthUnit> q4 = new Quantity<>(2, LengthUnit.FEET);

        System.out.println("Divide: " + q3.divide(q4));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(10, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000, WeightUnit.GRAM);

        System.out.println("Weight Subtract: " + w1.subtract(w2));
        System.out.println("Weight Divide: " + w1.divide(w2));

        // ===== VOLUME =====
        Quantity<VolumeUnit> v1 = new Quantity<>(5, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500, VolumeUnit.MILLILITRE);

        System.out.println("Volume Subtract: " + v1.subtract(v2));
        System.out.println("Volume Divide: " + v1.divide(v2));
    }
}