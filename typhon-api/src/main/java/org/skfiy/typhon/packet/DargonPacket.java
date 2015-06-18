package org.skfiy.typhon.packet;

public class DargonPacket extends Packet{
    
    //箱子id
    private int aid;
    //问题ID
    private String qid;
    //答案
    private String answer;
    //点亮格子id
    private int gid;
    //战斗失败还是胜利
    private int result;
    
    private Object val;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
    
}
