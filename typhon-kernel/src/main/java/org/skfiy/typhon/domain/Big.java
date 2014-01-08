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
package org.skfiy.typhon.domain;

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.domain.item.DynamicItem;
import org.skfiy.typhon.domain.item.StaticItem;
import org.skfiy.typhon.util.SortedList;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Big {

    private static final int MIN_POS = 1;
    
    private final List<Node> nodeData = new SortedList<>();
    private int maxSize = 100;

    /**
     * 
     * @return 
     */
    public List<Node> getNodes() {
        return nodeData;
    }

    /**
     * 
     * @param nodes 
     */
    public void setNodes(List<Node> nodes) {
        this.nodeData.addAll(nodes);
    }

    /**
     * 
     * @return 
     */
    public int size() {
        return nodeData.size();
    }
    
    /**
     * 
     * @return 
     */
    public boolean isFull() {
        return (maxSize == size());
    }

    /**
     * 
     * @return 
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 
     * @param maxSize 
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     *
     * @param staticItem
     * @return
     */
    public boolean intoItem(StaticItem staticItem) {
        return intoItem(staticItem, 1);
    }
    
    /**
     *
     * @param staticItem
     * @param count
     * @return
     */
    public synchronized boolean intoItem(StaticItem staticItem, int count) {
        int c = count;
        if (isOverlapped(staticItem)) {
            Node[] nodes = findNodes(staticItem.getId());
            if (nodes.length > 0) {
                for (Node n : nodes) {
                    int av = n.item.getOverlapping() - n.total;
                    if (av > 0) {
                        c -= av;
                        if (c <= 0) {
                            n.total += count;
                            return true;
                        } else {
                            n.total += av;
                        }
                    }
                }
            }
        }

        if (c <= 0) {
            return true;
        }

        // 背包已满无法放置新的道具
        if (isFull()) {
            return false;
        }

        int pos = nextPos();
        if (pos <= 0) {
            return false;
        }

        Node node = new Node();
        node.setPos(pos);
        node.setItem(staticItem.toDynamicItem());
        node.setTotal(count);
        nodeData.add(node);
        return true;
    }
    
    /**
     * 
     * @param pos
     * @return 
     */
    public Node findNode(int pos) {
        for (Node node : nodeData) {
            if (node.pos == pos) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param iid
     * @return 
     */
    public Node findNode(String iid) {
        for (Node node : nodeData) {
            if (node.item.getId().equals(iid)) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param iid
     * @return 
     */
    public Node[] findNodes(String iid) {
        List<Node> ns = new ArrayList<>();
        for (Node node : nodeData) {
            if (node.getItem().getId().equals(iid)) {
                ns.add(node);
            }
        }
        
        Node[] nodeArray = new Node[ns.size()];
        ns.toArray(nodeArray);
        return nodeArray;
    }
    
    /**
     * 
     * @param pos
     * @return 
     */
    public synchronized Node removeNode(int pos) {
        for (int i = 0; i < nodeData.size(); i++) {
            Node node = nodeData.get(i);
            if (node.getPos() == pos) {
                return nodeData.remove(i);
            }
        }
        return null;
    }
    
    /**
     * 
     * @param node
     * @return 
     */
    public synchronized boolean removeNode(Node node) {
        return nodeData.remove(node);
    }
    
    /**
     *
     * @param srcPos
     * @param destPost
     * @return 
     */
    public synchronized boolean swap(int srcPos, int destPost) {
        if (destPost > maxSize) {
            return false;
        }

        Node srcNode = findNode(srcPos);
        if (srcNode == null) {
            return false;
        }

        Node destNode = findNode(destPost);
        if (destNode != null) {
            destNode.setPos(srcPos);
            nodeData.remove(destNode);
            nodeData.add(destNode);
        }
        
        srcNode.setPos(destPost);
        nodeData.remove(srcNode);
        nodeData.add(srcNode);
        return true;
    }
    
    /**
     * 
     * @param node
     * @param count
     * @return 
     */
    public synchronized boolean decrementTotal(Node node, int count) {
        Assert.notNull(node);
        
        if (node.getTotal() < count) {
            return false;
        }
        
        int newTotal = node.getTotal() - count;
        if (newTotal <= 0) {
            removeNode(node);
        } else {
            node.setTotal(newTotal);
        }
        return true;
    }
    
    /**
     *
     * @param iid
     * @param count
     * @return
     */
    public synchronized boolean decrementTotal(String iid, int count) {
        Node[] nodes = findNodes(iid);
        
        int allCount = 0;
        for (Node node : nodes) {
            allCount += node.getTotal();
        }
        
        if (allCount < count) {
            return false;
        }
        
        int newTotal;
        for (Node node : nodes) {
            newTotal = node.getTotal() - count;
            if (newTotal <= 0) {
                removeNode(node);
                
                if (newTotal == 0) {
                    break;
                }
                count = -(newTotal);
            } else {
                node.setTotal(newTotal);
                break;
            }
        }
        return true;
    }
    
    /**
     * 
     * @return 
     */
    private int nextPos() {
        if (nodeData.isEmpty()) {
            return MIN_POS;
        }
        
        Node prevNode = null;
        for (int i = 0; i < nodeData.size(); i++) {
            Node node = nodeData.get(i);
            // 如果上一个节点为null, 并且第一个元素pos不是从MIN_POS开始的
            if (prevNode == null) {
                if (node.getPos() > MIN_POS) {
                    return MIN_POS;
                }
            } else {
                // 如果当前节点与上一个节点的pos差距大于1则返回之间的pos
                if ((node.getPos() - prevNode.getPos()) > 1) { 
                    return prevNode.getPos() + 1;
                }
            }
            prevNode = node;
        }
        
        if (prevNode.getPos() < maxSize) {
            return (prevNode.getPos() + 1);
        }
        return -1;
    }
    
    /**
     * 
     * @param staticItem
     * @return 
     */
    private boolean isOverlapped(StaticItem staticItem) {
        return (staticItem.getOverlapping() != 1);
    }
    
    /**
     * 
     */
    public static class Node implements Comparable<Node> {

        private int pos;
        private DynamicItem item;
        private int total;

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public DynamicItem getItem() {
            return item;
        }

        public void setItem(DynamicItem item) {
            this.item = item;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(pos, o.pos);
        }
    }

}
