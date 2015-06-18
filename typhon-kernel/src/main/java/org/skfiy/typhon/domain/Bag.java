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
import java.util.Collections;
import java.util.List;

import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.util.DomainUtils;
import org.skfiy.typhon.util.SortedList;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 背包实体对象设置.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"full"})
public class Bag extends AbstractChangeable {

    private static final int MIN_POS = 1;

    private final List<Node> nodes = new SortedList<>();
    private int maxSize = Integer.MAX_VALUE;
    
    /**
     *
     * @return
     */
    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * 
     * @param nodes
     */
    public void setNodes(List<Node> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
        
        for (Node n : this.nodes) {
            n.set(this, "nodes");
        }
    }

    /**
     * 获取背包最大容易, 默认值为100.
     *
     * @return 一个无符号的数字
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 设置背包最大容量.
     *
     * @param maxSize 一个无符号的数字
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 往背包里面放置一个道具并且返回是否放置成功.
     * 
     * @param itemDobj 道具对象
     * @return 是否放置成功
     */
    public int intoItem(ItemDobj itemDobj) {
        return intoItem(itemDobj, 1);
    }

    /**
     * 往背包里面放置指定道具数量并且返回是否放置成功. 该接口发现ItemDobj如果是可堆叠的会自动叠加到现有的
     * 道具中去.
     *
     * @param itemDobj 道具对象
     * @param count 道具数量
     * @return 是否放置成功
     */
    public synchronized int intoItem(ItemDobj itemDobj, int count) {
        int c = count;
        if (isOverlapped(itemDobj)) {
            Node n = findNode(itemDobj.getId());
            if (n != null) {
                int av = n.item.getOverlapping() - n.total;
                if (av > 0) {
                    c -= av;
                    if (c <= 0) {
                        n.setTotal(n.total + count);
                    } else {
                        n.setTotal(n.total + av);
                    }
                    n.setLastGainTime((int) (System.currentTimeMillis() / 1000));
                }
                return n.pos;
            }
            
            if (c > itemDobj.getOverlapping()) {
                c = itemDobj.getOverlapping();
            }
        }

        if (c <= 0) {
            return -1;
        }

        // 背包已满无法放置新的道具
        if (isFull()) {
            return -1;
        }

        if (!isOverlapped(itemDobj) && c > 1) {
            for (int i = 0; i < c; i++) {
                addNode(itemDobj, 1);
            }
        } else {
            return addNode(itemDobj, c);
        }

//        int pos = nextPos();
//        if (pos < MIN_POS) {
//            return -1;
//        }
//        
//        Node node = new Node();
//        node.setPos(pos);
//        node.setItem(itemDobj.toDomainItem());
//        node.setTotal(c);
//        node.setLastGainTime((int) (System.currentTimeMillis() / 1000));
//        
//        nodes.add(node);
//        
//        // 通知客户端添加一个道具
//        DomainUtils.fireIndexPropertyAdd(this, "nodes", node);
//        
//        node.set(this, "nodes");
        return -1;
    }

    /**
     * 查询指定位置的道具信息.
     *
     * @param pos 道具位置
     * @return 道具信息
     */
    public Node findNode(int pos) {
        for (Node node : nodes) {
            if (node.pos == pos) {
                return node;
            }
        }
        return null;
    }

    /**
     * 根据道具ID查询背包符合条件的第一个道具信息.
     *
     * @param iid 道具ID
     * @return 第一个符合条件的道具信息
     */
    public Node findNode(String iid) {
        for (Node node : nodes) {
            if (node.item.getId().equals(iid)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 根据道具ID查询背包里面的具体节点道具信息.
     *
     * @param iid 道具ID
     * @return 符合条件的背包节点道具
     */
    public Node[] findNodes(String iid) {
        List<Node> ns = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getItem().getId().equals(iid)) {
                ns.add(node);
            }
        }

        Node[] nodeArray = new Node[ns.size()];
        ns.toArray(nodeArray);
        return nodeArray;
    }

    /**
     * 移除背包某个位置的道具并且返回该位置对应的具体道具对象.
     *
     * @param pos 需要移除的位置
     * @return {@code pos }位置对应的具体道具对象
     */
    public synchronized Node removeNode(int pos) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.getPos() == pos) {
                nodes.remove(i);
                DomainUtils.fireIndexPropertyRemove(node);
                return node;
            }
        }
        return null;
    }

    /**
     * 移除背包中的某个节点道具.
     *
     * @param node 具体的道具
     * @return 是否移除成功
     */
    public synchronized boolean removeNode(Node node) {
        boolean r = nodes.remove(node);
        if (r) {
            DomainUtils.fireIndexPropertyRemove(node);
        }
        return r;
    }

    /**
     * 交换两个位置的道具. {@code srcPos }必须是一个有效的位置并且该位置存在一个道具.
     *
     * @param srcPos 原始位置
     * @param destPost 目标位置
     * @return 是否交换成功
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
            nodes.remove(destNode);
            nodes.add(destNode);
        }

        srcNode.setPos(destPost);
        nodes.remove(srcNode);
        nodes.add(srcNode);
        return true;
    }

    /**
     * 减少某一个道具节点的数量.
     *
     * @param node 被减少的道具
     * @param count 减少的数量
     * @return 是否减少成功
     */
    public synchronized boolean decrementTotal(Node node, int count) {
        if (node == null || node.getTotal() < count) {
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
     * 减少背包中某种道具类型的数量.
     *
     * @param iid 道具ID
     * @param count 减少的数量
     * @return 是否减少成功
     */
    public synchronized boolean decrementTotal(String iid, int count) {
        Node[] nodeData = findNodes(iid);

        int allCount = 0;
        for (Node node : nodeData) {
            allCount += node.getTotal();
        }

        if (allCount < count) {
            return false;
        }

        int newTotal;
        for (Node node : nodeData) {
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
     * 背包道具的数量.
     * 
     * @return 数量
     */
    public int size() {
        return nodes.size();
    }

    /**
     * 背包是否已满.
     * 
     * @return 是/否
     */
    public boolean isFull() {
        return (maxSize == size());
    }
    
    private int addNode(ItemDobj itemDobj, int c) {
        int pos = nextPos();
        if (pos < MIN_POS) {
            return -1;
        }

        Node node = new Node();
        node.setPos(pos);
        node.setItem(itemDobj.toDomainItem());
        node.setTotal(c);
        node.setLastGainTime((int) (System.currentTimeMillis() / 1000));

        nodes.add(node);

        // 通知客户端添加一个道具
        DomainUtils.fireIndexPropertyAdd(this, "nodes", node);

        node.set(this, "nodes");
        return pos;
    }
    
    /**
     * 放置新道具的位置. 如果收到的返回值为-1则不是一个有效的位置, 此时背包无法容纳更多的道具.
     *
     * @return 道具的位置, 返回值大于等于1并且小于等余{@link #getMaxSize() }时为一个有效索引
     */
    private int nextPos() {
        if (nodes.isEmpty()) {
            return MIN_POS;
        }

        Node prevNode = null;
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            // 如果上一个节点为null, 并且第一个元素pos不是从MIN_POS
            if (prevNode == null) {
                if (node == null || node.getPos() > MIN_POS) {
                    return MIN_POS;
                }
            } else {
                // 如果当前节点与上节点的pos差距大于1则返回之间的pos
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

    private boolean isOverlapped(ItemDobj itemDobj) {
        return (itemDobj.getOverlapping() > 1);
    }

    /**
     *
     */
    @JSONType(ignores = {"index"})
    public static class Node extends AbstractIndexable implements Comparable<Node> {

        private int pos;
        private AbstractItem item;
        private int total;
        private int lastGainTime;
        
        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            DomainUtils.firePropertyChange(this, "pos", pos);
            this.pos = pos;
        }

        public <T extends AbstractItem> T getItem() {
            return (T) item;
        }

        public void setItem(AbstractItem item) {
            this.item = item;
            
            // FIXME 设置道具的Parent属性
            this.item.set(this, "item");
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
            
            DomainUtils.firePropertyChange(this, "total", total);
        }

        public int getLastGainTime() {
            return lastGainTime;
        }

        public void setLastGainTime(int lastGainTime) {
            this.lastGainTime = lastGainTime;
            DomainUtils.firePropertyChange(this, "lastGainTime", lastGainTime);
        }

        // ==================================================================================
        @Override
        public String parentPropertyName() {
            return "nodes";
        }

        @Override
        public int index() {
            return pos;
        }
        // ==================================================================================
        
        @Override
        public int compareTo(Node o) {
            return Integer.compare(pos, o.pos);
        }
    }

}
