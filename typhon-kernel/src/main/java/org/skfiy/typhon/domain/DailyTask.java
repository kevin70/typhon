package org.skfiy.typhon.domain;

import org.skfiy.typhon.util.DomainUtils;

public class DailyTask extends AbstractChangeable{

    private int taskHpveCounts;
    private int taskHdpveCounts;
    private int taskSpveCounts;
    private int taskActivities;
    private int taskDargonCounts;
    private int taskPvpCounts;
    private int taskEnchants;
    private int taskLotteries;
    private int taskAccessVigor;
    private int taskTree;
    private int taskTroopStreng;
    private int taskHardenStreng;
    private int taskCaravan;
    private int taskSocietyBoss;

    public int getTaskActivities() {
        return taskActivities;
    }

    public void setTaskActivities(int taskActivities) {
        this.taskActivities = taskActivities;
        DomainUtils.firePropertyChange(this, "taskActivities", taskActivities);
    }

    public int getTaskHpveCounts() {
        return taskHpveCounts;
    }

    public void setTaskHpveCounts(int taskHpveCounts) {
        this.taskHpveCounts = taskHpveCounts;
        DomainUtils.firePropertyChange(this, "taskHpveCounts", taskHpveCounts);
    }

    public int getTaskHdpveCounts() {
        return taskHdpveCounts;
    }

    public void setTaskHdpveCounts(int taskHdpveCounts) {
        this.taskHdpveCounts = taskHdpveCounts;
        DomainUtils.firePropertyChange(this, "taskHdpveCounts", taskHdpveCounts);
    }

    public int getTaskSpveCounts() {
        return taskSpveCounts;
    }

    public void setTaskSpveCounts(int taskSpveCounts) {
        this.taskSpveCounts = taskSpveCounts;
        DomainUtils.firePropertyChange(this, "taskSpveCounts", taskSpveCounts);
    }

    public int getTaskDargonCounts() {
        return taskDargonCounts;
    }

    public void setTaskDargonCounts(int taskDargonCounts) {
        this.taskDargonCounts = taskDargonCounts;
        DomainUtils.firePropertyChange(this, "taskDargonCounts", taskDargonCounts);
    }

    public int getTaskPvpCounts() {
        return taskPvpCounts;
    }

    public void setTaskPvpCounts(int taskPvpCounts) {
        this.taskPvpCounts = taskPvpCounts;
        DomainUtils.firePropertyChange(this, "taskPvpCounts", taskPvpCounts);
    }

    public int getTaskEnchants() {
        return taskEnchants;
    }

    public void setTaskEnchants(int taskEnchants) {
        this.taskEnchants = taskEnchants;
        DomainUtils.firePropertyChange(this, "taskEnchants", taskEnchants);
    }

    
    public int getTaskLotteries() {
        return taskLotteries;
    }

    public void setTaskLotteries(int taskLotteries) {
        this.taskLotteries = taskLotteries;
        DomainUtils.firePropertyChange(this, "taskLotteries", taskLotteries);
    }
    

    public int getTaskAccessVigor() {
        return taskAccessVigor;
    }

    public void setTaskAccessVigor(int taskAccessVigor) {
        this.taskAccessVigor = taskAccessVigor;
        DomainUtils.firePropertyChange(this, "taskAccessVigor", taskAccessVigor);
    }
    public int getTaskTree() {
        return taskTree;
    }
    
    public void setTaskTree(int taskTree) {
        this.taskTree = taskTree;
        DomainUtils.firePropertyChange(this, "taskTree", taskTree);
    }

    public int getTaskTroopStreng() {
        return taskTroopStreng;
    }

    public void setTaskTroopStreng(int taskTroopStreng) {
        this.taskTroopStreng = taskTroopStreng;
        DomainUtils.firePropertyChange(this, "taskTroopStreng", taskTroopStreng);
    }
    
    public int getTaskHardenStreng() {
        return taskHardenStreng;
    }

    public void setTaskHardenStreng(int taskHardenStreng) {
        this.taskHardenStreng = taskHardenStreng;
        DomainUtils.firePropertyChange(this, "taskHardenStreng", taskHardenStreng);
    }
    

    public int getTaskCaravan() {
        return taskCaravan;
    }

    public void setTaskCaravan(int taskCaravan) {
        this.taskCaravan = taskCaravan;
        DomainUtils.firePropertyChange(this, "taskCaravan", taskCaravan);
    }

    public int getTaskSocietyBoss() {
        return taskSocietyBoss;
    }

    public void setTaskSocietyBoss(int taskSocietyBoss) {
        this.taskSocietyBoss = taskSocietyBoss;
        DomainUtils.firePropertyChange(this, "taskSocietyBoss", taskSocietyBoss);
    }

    public void cleanProperties() {
        setTaskActivities(0);
        setTaskLotteries(0);
        setTaskPvpCounts(0);
        setTaskDargonCounts(0);
        setTaskEnchants(0);
        setTaskHdpveCounts(0);
        setTaskHpveCounts(0);
        setTaskSpveCounts(0);
        setTaskAccessVigor(0);
        setTaskTree(0);
        setTaskTroopStreng(0);
        setTaskHardenStreng(0);
        setTaskCaravan(0);
        setTaskSocietyBoss(0);
    }
}
