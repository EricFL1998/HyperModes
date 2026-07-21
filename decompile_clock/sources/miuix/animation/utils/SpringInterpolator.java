package miuix.animation.utils;

import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes2.dex */
public class SpringInterpolator implements Interpolator {
    private float acceleration;
    private float dampingRatio;
    private long duration;
    private long fakeDuration;
    private double g;
    private float inputScale;
    private float mass;
    private double omega;
    private final double overDampThreshold;
    private double p;
    private double q;
    private float response;
    private SpringSolution solution;
    private final double underDampThreshold;
    private float velocity;
    private final double velocityThreshold;
    private double xStar;
    private double zeta;

    public SpringInterpolator() {
        this(0.85f, 0.3f);
    }

    public SpringInterpolator(float f, float f2) {
        this(f, f2, 1.0f);
    }

    public SpringInterpolator(float f, float f2, float f3) {
        this(f, f2, f3, 0.0f);
    }

    public SpringInterpolator(float f, float f2, float f3, float f4) {
        this.underDampThreshold = 1.0E-4d;
        this.overDampThreshold = 0.001d;
        this.velocityThreshold = 5.0E-4d;
        this.fakeDuration = 1000L;
        this.duration = 1000L;
        this.inputScale = 1.0f;
        this.velocity = 0.0f;
        this.dampingRatio = f;
        this.response = f2;
        this.mass = f3;
        this.acceleration = f4;
        updateParameters();
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (f == 1.0f) {
            return 1.0f;
        }
        float f2 = f * this.inputScale;
        float fX = (float) this.solution.x(f2);
        this.velocity = (float) this.solution.dX(f2);
        return fX;
    }

    public float getDamping() {
        return this.dampingRatio;
    }

    public float getResponse() {
        return this.response;
    }

    public SpringInterpolator setDampingAndResponse(float f, float f2) {
        this.dampingRatio = f;
        this.response = f2;
        updateParameters();
        return this;
    }

    public SpringInterpolator setResponse(float f) {
        this.response = f;
        updateParameters();
        return this;
    }

    public SpringInterpolator setDamping(float f) {
        this.dampingRatio = f;
        updateParameters();
        return this;
    }

    public SpringInterpolator setMass(float f) {
        this.mass = f;
        updateParameters();
        return this;
    }

    public SpringInterpolator setAcceleration(float f) {
        this.acceleration = f;
        updateParameters();
        return this;
    }

    public SpringInterpolator setFakeDuration(long j) {
        this.fakeDuration = j;
        updateParameters();
        this.inputScale = this.fakeDuration / 1000.0f;
        return this;
    }

    public long getDuration() {
        return this.duration;
    }

    private void updateParameters() {
        double d = this.dampingRatio;
        this.zeta = d;
        double d2 = 6.283185307179586d / ((double) this.response);
        this.omega = d2;
        float f = this.mass;
        double d3 = (((d * 2.0d) * d2) * ((double) f)) / ((double) f);
        this.p = d3;
        double d4 = ((d2 * d2) * ((double) f)) / ((double) f);
        this.q = d4;
        double d5 = this.acceleration;
        this.g = d5;
        double d6 = ((-d5) / d4) + 1.0d;
        this.xStar = d6;
        double d7 = (d3 * d3) - (d4 * 4.0d);
        double d8 = 0.0d - d6;
        if (d7 > 0.0d) {
            this.solution = new OverDampingSolution(d7, d8, this.p, this.velocity, this.xStar);
        } else if (d7 == 0.0d) {
            this.solution = new CriticalDampingSolution(d7, d8, this.p, this.velocity, this.xStar);
        } else {
            this.solution = new UnderDampingSolution(d7, d8, this.p, this.velocity, this.xStar);
        }
        long jSolveDuration = (long) (solveDuration(d7) * 1000.0d);
        this.duration = jSolveDuration;
        this.inputScale = jSolveDuration / 1000.0f;
    }

    private double solveDuration(double d) {
        double d2;
        double dX = 0.0d;
        double d3 = d >= 0.0d ? 0.001d : 1.0E-4d;
        double d4 = this.g;
        double d5 = 1.0d;
        if (d4 == 0.0d) {
            float f = 0.0f;
            while (Math.abs(dX - 1.0d) > d3) {
                f += 0.001f;
                dX = this.solution.x(f);
                double dDX = this.solution.dX(f);
                if (Math.abs(dX - 1.0d) <= d3 && dDX <= 5.0E-4d) {
                    break;
                }
            }
            return f;
        }
        double dSolve = this.solution.solve(0.0d, this.q, d4, this.xStar);
        double d6 = this.q;
        double d7 = this.xStar;
        double d8 = d6 * d7 * d7;
        double d9 = (dSolve - d8) * d3;
        double d10 = 1.0d;
        double dSolve2 = this.solution.solve(1.0d, d6, this.g, d7);
        double d11 = 0.0d;
        while (true) {
            d2 = d8 + d9;
            if (dSolve2 <= d2) {
                break;
            }
            double d12 = d10 + d5;
            d11 = d10;
            d5 = 1.0d;
            d10 = d12;
            dSolve2 = this.solution.solve(d12, this.q, this.g, this.xStar);
            d9 = d9;
        }
        do {
            double d13 = (d11 + d10) / 2.0d;
            if (this.solution.solve(d13, this.q, this.g, this.xStar) > d2) {
                d11 = d13;
            } else {
                d10 = d13;
            }
        } while (d10 - d11 >= d3);
        return d10;
    }

    static abstract class SpringSolution {
        abstract double dX(float f);

        abstract double x(float f);

        SpringSolution() {
        }

        double solve(double d, double d2, double d3, double d4) {
            float f = (float) d;
            double dX = x(f);
            double dDX = dX(f);
            return (((d2 * dX) * dX) + (dDX * dDX)) - ((d3 * 2.0d) * (dX - d4));
        }
    }

    static class OverDampingSolution extends SpringSolution {
        private final double c1;
        private final double c2;
        private final double r1;
        private final double r2;
        private final double xStar;

        OverDampingSolution(double d, double d2, double d3, double d4, double d5) {
            double dSqrt = Math.sqrt(d);
            double d6 = (dSqrt - d3) / 2.0d;
            this.r1 = d6;
            double d7 = ((-dSqrt) - d3) / 2.0d;
            this.r2 = d7;
            this.c1 = (d4 - (d2 * d7)) / dSqrt;
            this.c2 = (-(d4 - (d6 * d2))) / dSqrt;
            this.xStar = d5;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double x(float f) {
            double d = f;
            return (this.c1 * Math.exp(this.r1 * d)) + (this.c2 * Math.exp(this.r2 * d)) + this.xStar;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double dX(float f) {
            double d = this.c1;
            double d2 = this.r1;
            double d3 = f;
            double dExp = d * d2 * Math.exp(d2 * d3);
            double d4 = this.c2;
            double d5 = this.r2;
            return dExp + (d4 * d5 * Math.exp(d5 * d3));
        }
    }

    static class CriticalDampingSolution extends SpringSolution {
        private final double c1;
        private final double c2;
        private final double r;
        private final double xStar;

        CriticalDampingSolution(double d, double d2, double d3, double d4, double d5) {
            double d6 = (-d3) / 2.0d;
            this.r = d6;
            this.c1 = d2;
            this.c2 = d4 - (d2 * d6);
            this.xStar = d5;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double x(float f) {
            double d = f;
            return ((this.c1 + (this.c2 * d)) * Math.exp(this.r * d)) + this.xStar;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double dX(float f) {
            double d = this.c1;
            double d2 = this.r;
            double d3 = f;
            return ((d * d2) + (this.c2 * ((d2 * d3) + 1.0d))) * Math.exp(d2 * d3);
        }
    }

    static class UnderDampingSolution extends SpringSolution {
        private final double alpha;
        private final double beta;
        private final double c1;
        private final double c2;
        private final double xStar;

        UnderDampingSolution(double d, double d2, double d3, double d4, double d5) {
            double d6 = (-d3) / 2.0d;
            this.alpha = d6;
            double dSqrt = Math.sqrt(-d) / 2.0d;
            this.beta = dSqrt;
            this.c1 = d2;
            this.c2 = (d4 - (d2 * d6)) / dSqrt;
            this.xStar = d5;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double x(float f) {
            double d = f;
            return (Math.exp(this.alpha * d) * ((this.c1 * Math.cos(this.beta * d)) + (this.c2 * Math.sin(this.beta * d)))) + this.xStar;
        }

        @Override // miuix.animation.utils.SpringInterpolator.SpringSolution
        double dX(float f) {
            double d = f;
            double dExp = Math.exp(this.alpha * d);
            double d2 = this.c1 * this.alpha;
            double d3 = this.c2;
            double d4 = this.beta;
            double dCos = (d2 + (d3 * d4)) * Math.cos(d4 * d);
            double d5 = this.c2 * this.alpha;
            double d6 = this.c1;
            double d7 = this.beta;
            return dExp * (dCos + ((d5 - (d6 * d7)) * Math.sin(d7 * d)));
        }
    }
}
