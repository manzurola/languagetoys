package com.github.manzurola.languagetoys.common.score;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * A score represents a double value in the range [0, 1].
 * Only 3 digits after decimal point are preserved.
 */
public final class Score implements Comparable<Score> {

    public static final Score Bottom = new Score(0);
    public static final Score Top = new Score(1);
    private final double value;

    public Score(double value) {
        validateRange(value);
        this.value = Double.parseDouble(new DecimalFormat("#.###").format(value));
    }

    public static Score points(int points) {
        double max = Top.value * 100;
        if (points >= max) {
            return Top;
        }
        if (points < 0) {
            throw new IllegalArgumentException();
        }
        return of(points * 100);
    }

    public static Score of(double value) {
        return new Score(value);
    }

    public static Score cap(double value) {
        if (value < 0.0) return of(0.0);
        if (value > 1.0) return of(1.0);
        return of(value);
    }

    private void validateRange(double value) {
        if (value < 0d || value > 1d || Double.isNaN(value)) {
            throw new IllegalArgumentException(
                    String.format("Value %s not within range [0,1]", value));
        }
    }

    public final double value() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score that = (Score) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public final String toString() {
        return String.valueOf(value);
    }

    @Override
    public final int compareTo(Score o) {
        return Double.compare(this.value, o.value);
    }
}
