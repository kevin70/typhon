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

import com.alibaba.fastjson.annotation.JSONType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.util.SortedList;
import org.skfiy.util.Assert;

/**
 * 背包实体对象设置.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = "player")
public class Bag implements Changeable {

    private static final int MIN_POS = 1;

    private final List<Node> nodeData = new SortedList<>();
    private int maxSize = 100;
    
    private Player player;

    @Override
    public String getNs() {
        return Namespaces.BIG;
    }

    @Override
    public final Player getPlayer() {
        return player;
    }

    @Override
    public final void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *
     * @return
     */
    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodeData);
    }

    /**
     * 
     * @param nodes
     */
    public void setNodes(List<Node> nodes) {
        this.nodeData.clear();
        this.nodeData.addAll(nodes);
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
    public boolean intoItem(ItemDobj itemDobj) {
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
    public synchronized boolean intoItem(ItemDobj itemDobj, int count) {
        int c = count;
        if (isOverlapped(itemDobj)) {
            Node[] nodes = findNodes(itemDobj.getId());
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
        node.setItem(itemDobj.toDomainItem());
        node.setTotal(count);
        nodeData.add(node);
        return true;
    }

    /**
     * 查询指定位置的道具信息.
     *
     * @param pos 道具位置
     * @return 道具信息
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
     * 根据道具ID查询背包符合条件的第一个道具信息.
     *
     * @param iid 道具ID
     * @return 第一个符合条件的道具信息
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
     * 根据道具ID查询背包里面的具体节点道具信息.
     *
     * @param iid 道具ID
     * @return 符合条件的背包节点道具
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
     * 移除背包某个位置的道具并且返回该位置对应的具体道具对象.
     *
     * @param pos 需要移除的位置
     * @return {@code pos }位置对应的具体道具对象
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
     * 移除背包中的某个节点道具.
     *
     * @param node 具体的道具
     * @return 是否移除成功
     */
    public synchronized boolean removeNode(Node node) {
        return nodeData.remove(node);
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
            nodeData.remove(destNode);
            nodeData.add(destNode);
        }

        srcNode.setPos(destPost);
        nodeData.remove(srcNode);
        nodeData.add(srcNode);
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
     * 减少背包中某种道具类型的数量.
     *
     * @param iid 道具ID
     * @param count 减少的数量
     * @return 是否减少成功
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
     * 背包道具的数量.
     * 
     * @return 数量
     */
    public int size() {
        return nodeData.size();
    }

    /**
     * 背包是否已满.
     * 
     * @return 是/否
     */
    public boolean isFull() {
        return (maxSize == size());
    }
    
    /**
     * 放置新道具的位置. 如果收到的返回值为-1则不是一个有效的位置, 此时背包无法容纳更多的道具.
     *
     * @return 道具的位置, 返回值大于等于1并且小于等余{@link #getMaxSize() }时为一个有效索引
     */
    private int nextPos() {
        if (nodeData.isEmpty()) {
            return MIN_POS;
        }

        Node prevNode = null;
        for (int i = 0; i < nodeData.size(); i++) {
            Node node = nodeData.get(i);
            // 如果上一个节点为null, 并且第一个元素pos不是从MIN_POS�?���?
            if (prevNode == null) {
                if (node.getPos() > MIN_POS) {
                    return MIN_POS;
                }
            } else {
                // 如果当前节点与上�?��节点的pos差距大于1则返回之间的pos
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
    public static class Node implements Comparable<Node> {

        private int pos;
        private AbstractItem item;
        private int total;

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public AbstractItem getItem() {
            return item;
        }

        public void setItem(AbstractItem item) {
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
