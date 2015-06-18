/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.util;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class FastRandom extends Random implements Serializable {

    private static final long serialVersionUID = 6910932436509951204L;

    private static final ThreadLocal<FastRandom> THREAD_LOCAL = new ThreadLocal<FastRandom>() {
        @Override
        protected FastRandom initialValue() {
            return new FastRandom();
        }
    };

    //for initialization similar to java.util.Random
    // with corrections from http://www.alife.co.uk/nonrandom/proposal/index.html
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    private static final int Q_SIZE = 4096;
    private static final long a = 18782;
    private static final int r = 0xfffffffe;

    private int[] Q;
    private int c;
    private int idx;

    /**
     * Returns a thread-local FastRandom value.
     *
     * @return the thread-local random number generator.
     */
    public static FastRandom threadLocal() {
        return THREAD_LOCAL.get();
    }

    /**
     * Creates an RNG seeded with a system time.
     *
     * Creation of two fast randoms sequentially will most probably produce two different generators.
     */
    public FastRandom() {
        super();
    }

    /**
     * Creates an RNG seeded with the given seed of type {@code long}.
     *
     * @param seed the seed.
     */
    public FastRandom(long seed) {
        super(seed);
    }

    /**
     * Creates a copy of this RNG.
     *
     * These two generators will produce the same random sequence.
     *
     * @return the copy of this RNG.
     */
    public FastRandom makeCopy() {
        FastRandom rv = new FastRandom(1);
        rv.copyStateFrom(this);
        return rv;
    }

    /**
     * Sets this RNG to exactly the same state as of the specified RNG.
     *
     * The next chain of
     *
     * @param source the RNG to copy the state from.
     */
    public void copyStateFrom(FastRandom source) {
        this.c = source.c;
        this.idx = source.idx;
        System.arraycopy(source.Q, 0, this.Q, 0, Q_SIZE);
    }

    /**
     * Seeds the RNG with the given seed of type {@code long}.
     *
     * Unlike some implementations of {@link Random}, a {@link FastRandom} either constructed using a seed {@code S} or
     * initialized to {@code S} later will produce the same random sequence.
     *
     * @param seed the seed.
     */
    @Override
    public void setSeed(long seed) {
        super.setSeed(seed);
        if (Q == null) {
            Q = new int[Q_SIZE];
        }
        seed = nextSeed(seed);
        c = ((int) (seed >>> 16)) % (809430660);
        seed = nextSeed(seed);
        for (int i = 0; i < Q_SIZE; ++i) {
            Q[i] = (int) (seed >>> 16);
            seed = nextSeed(seed);
        }
        this.idx = 0;
    }

    private long nextSeed(long seed) {
        return (seed & mask) * multiplier + addend + (seed >>> 47);
    }

    @Override
    protected int next(int nBits) {
        idx = (idx + 1) & (Q_SIZE - 1);
        long t = a * Q[idx] + c;
        c = (int) (t >>> 32);
        int x = (int) t + c;
        if (x < c) {
            x++;
            c++;
        }
        int rv = Q[idx] = r - x;
        return rv >>> (32 - nBits);
    }
}
