///*
// * Copyright 2014 The Skfiy Open Association.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package war;
//
//import org.apache.commons.lang3.ArrayUtils;
//import org.skfiy.typhon.script.Script;
//import org.skfiy.typhon.session.Session;
//import org.skfiy.typhon.spi.pve.Member;
//import org.skfiy.typhon.spi.pve.PveAi;
//import org.skfiy.typhon.util.FastRandom;
//
///**
// *
// * @author Kevin Zou <kevinz@skfiy.org>
// */
//public class AiFactoryScript implements Script {
//
//    @Override
//    public Object invoke(Session session, Object obj) {
//        Member member = (Member) obj;
//
//        PveAi[] ais = new PveAi[member.getAis().length];
//        for (int i = 0; i < ais.length; i++) {
//            String ai = member.getAis()[i];
//            switch (ai) {
//                case "S1":
//                    ais[i] = new AiS1();
//                    break;
//                case "S2":
//                    ais[i] = new AiS2();
//                    break;
//                case "S3":
//                    ais[i] = new AiS3();
//                    break;
//                case "A1":
//                    ais[i] = new AiA1();
//                    break;
//                case "A2":
//                    ais[i] = new AiA2();
//                    break;
//                case "B1":
//                    ais[i] = new AiB1();
//                    break;
//                case "B2":
//                    ais[i] = new AiB2();
//                    break;
//                case "C1":
//                    ais[i] = new AiC1();
//                    break;
//                case "C2":
//                    ais[i] = new AiC2();
//                    break;
//                case "D1":
//                    ais[i] = new AiD1();
//                    break;
//                case "D2":
//                    ais[i] = new AiD2();
//                    break;
//                default:
//                    throw new NullPointerException("No [" + ai + "] AI");
//            }
//        }
//
//        return ais;
//    }
//
//    private static abstract class AbstractAi implements PveAi {
//
//        private final FastRandom random;
//        private final double[] _orl_rates;
//        /**
//         * <pre>
//         * 0: GJi
//         * 1: QXi
//         * 2: Qi
//         * 3: BSa
//         * 4: Miss
//         * </pre>
//         */
//        private double[] rates;
//
//        AbstractAi(double[] rates) {
//            random = new FastRandom();
//            this._orl_rates = rates;
//
//            this.rates = ArrayUtils.clone(rates);
//        }
//
//        @Override
//        public String ranAiSkill(Object obj) {
//            int i;
//            for (i = 0; i < rates.length; i++) {
//                if (rates[i] <= random.nextInt(100) + 1) {
//                    break;
//                }
//            }
//
//            if (isReset(i)) {
//                this.rates = ArrayUtils.clone(_orl_rates);
//            } else {
//                for (int j = 0; j < rates.length; j++) {
//                    rates[i] = rates[i] + getRatePlus(i);
//                }
//            }
//
//            switch (i) {
//                case 0:
//                    return PveAi.SKILL_GJI;
//                case 1:
//                    return PveAi.SKILL_QXI;
//                case 2:
//                    return PveAi.SKILL_QI;
//                case 3:
//                    return PveAi.SKILL_BSA;
//                default:
//                    return PveAi.SKILL_MISS;
//            }
//        }
//
//        protected abstract boolean isReset(int i);
//
//        protected abstract int getRatePlus(int i);
//
//    }
//
//    private static class AiS1 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {0, 0, 40, 0, 60};
//        private static final int[] RATE_PLUS = {0, 0, 10, 0, -10};
//
//        AiS1() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "S1";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================S1 End===========================================================
//
//    private static class AiS2 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {60, 20, 40, 0, 0};
//        private static final int[] RATE_PLUS = {-30, -10, 40, 0, 0};
//
//        AiS2() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "S2";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================S2 End===========================================================
//
//    private static class AiS3 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {10, 50, 0, 40, 0};
//        private static final int[] RATE_PLUS = {-10, -50, 0, 60, 0};
//
//        AiS3() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "S3";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 3);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================S3 End===========================================================
//
//    private static class AiA1 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {60, 15, 25, 0, 0};
//        private static final int[] RATE_PLUS = {-20, -5, 25, 0, 0};
//
//        AiA1() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "A1";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================A1 End===========================================================
//
//    private static class AiA2 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {0, 30, 0, 20, 50};
//        private static final int[] RATE_PLUS = {0, -15, 0, 40, -25};
//
//        AiA2() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "A2";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 3);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================A2 End===========================================================
//
//    private static class AiB1 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {50, 35, 15, 0, 0};
//        private static final int[] RATE_PLUS = {-10, -7, 15, 0, 0};
//
//        AiB1() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "B1";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================B1 End===========================================================
//
//    private static class AiB2 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {0, 30, 0, 20, 50};
//        private static final int[] RATE_PLUS = {0, -15, 0, 40, -25};
//
//        AiB2() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "B2";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 3);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================B2 End===========================================================
//
//    private static class AiC1 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {80, 10, 10, 0, 0};
//        private static final int[] RATE_PLUS = {-16, -2, 18, 0, 0};
//
//        AiC1() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "C1";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================C1 End===========================================================
//
//    private static class AiC2 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {0, 10, 0, 20, 70};
//        private static final int[] RATE_PLUS = {0, -3, 0, 24, -21};
//
//        AiC2() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "C2";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 3);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================C2 End===========================================================
//
//    private static class AiD1 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {70, 20, 10, 0, 0};
//        private static final int[] RATE_PLUS = {-14, -4, 18, 0, 0};
//
//        AiD1() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "D1";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 2);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================D1 End===========================================================
//
//    private static class AiD2 extends AbstractAi {
//
//        private static final double[] DEFAULT_RATES = {0, 0, 0, 10, 90};
//        private static final int[] RATE_PLUS = {0, 0, 0, 15, -15};
//
//        AiD2() {
//            super(DEFAULT_RATES);
//        }
//
//        @Override
//        public String getName() {
//            return "D2";
//        }
//
//        @Override
//        protected boolean isReset(int i) {
//            return (i == 3);
//        }
//
//        @Override
//        protected int getRatePlus(int i) {
//            return RATE_PLUS[i];
//        }
//
//    }
//    // ================================================D2 End===========================================================
//}
