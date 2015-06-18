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
package org.skfiy.typhon.spi.war;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.skfiy.typhon.domain.item.IFightItem.Shot;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WarCombo implements Comparable<WarCombo> {

    private final Shot shot;
    private final Set<FightObject> fightObjects;
    private final List<Point> points;
    private int c = 0;

    public WarCombo(Shot shot) {
        this.shot = shot;
        this.points = new ArrayList<>();
        this.fightObjects = new HashSet<>(5);
    }

    public Shot getShot() {
        return shot;
    }

    public int getComboCount() {
        return (c / 3) + (c % 3);
    }
    
    public List<Point> getPoints() {
        return points;
    }
    
    public void addPoint(Point... points) {
        for (Point point : points) {
            if (!this.points.contains(point)) {
                this.points.add(point);
            }
            this.fightObjects.add(point.getFightObject());
            c++;
        }
    }

    public Set<FightObject> getFightObjects() {
        return fightObjects;
    }

    public int getFightObjectSize() {
        return fightObjects.size();
    }
    
    public String getAreaString() {
        String rs = null;
        for (Point p : points) {
            if (rs == null) {
                rs = p.getFightObject().getArea().name();
                continue;
            }

            if (!rs.equals(p.getFightObject().getArea().name())) {
                rs = "none";
                break;
            }
        }
        return rs;
    }
    
    @Override
    public int compareTo(WarCombo o) {
        if (o.shot == Shot.Q7 || o.shot == Shot.Miss) {
            return -1;
        }

        return Integer.compare(shot.ordinal(), o.shot.ordinal());
    }
    
    public static class Point {

        private final int x;
        private final int y;
        private final FightObject fightObject;

        public Point(int x, int y, FightObject fightObject) {
            this.x = x;
            this.y = y;
            this.fightObject = fightObject;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public FightObject getFightObject() {
            return fightObject;
        }
        
        @Override
        public int hashCode() {
            Hasher hasher = Hashing.murmur3_32().newHasher();
            hasher.putInt(x);
            hasher.putInt(y);
            return hasher.hash().asInt();
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Point) {
                Point p = (Point) obj;
                return (x == p.x && y == p.y);
            }
            return false;
        }
        
    }

}
