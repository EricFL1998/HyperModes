package miuix.animation.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class Piecewise implements Function {
    private final double[] durations;
    private final List<Function> functions;

    public Piecewise(int i) {
        this.functions = new ArrayList(i);
        this.durations = new double[i];
    }

    @Override // miuix.animation.function.Function
    public double apply(double d) {
        int iBinarySearch = Arrays.binarySearch(this.durations, d);
        if (iBinarySearch < 0) {
            iBinarySearch = (-iBinarySearch) - 1;
        }
        if (iBinarySearch >= this.functions.size()) {
            return Double.NaN;
        }
        if (iBinarySearch > 0) {
            d -= this.durations[iBinarySearch - 1];
        }
        return this.functions.get(iBinarySearch).apply(d);
    }

    public void add(Function function, double d) {
        this.durations[this.functions.size()] = d;
        this.functions.add(function);
    }
}
