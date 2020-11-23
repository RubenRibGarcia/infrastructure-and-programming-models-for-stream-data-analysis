package org.isel.thesis.impads.storm.low_level.topology.bolts.join;

import java.io.Serializable;

public class JoinCriteria implements Serializable {

    public static final long serialVersionUID = 1L;

    private JoinType joinType;        // nature of join
    private KeySelector current;      // field for the current stream
    private KeySelector other;      // field for the other (2nd) stream

    public JoinCriteria(KeySelector current) {
        this.joinType = null;
        this.current = current;
        this.other = null;
    }

    public JoinCriteria(KeySelector current, JoinCriteria otherStreamJoinInfo, JoinType joinType) {
        this.joinType = joinType;
        this.current = current;
        this.other = otherStreamJoinInfo.getCurrentFieldSelector();
    }

    public KeySelector getCurrentFieldSelector() {
        return current;
    }

    public KeySelector getOtherFieldSelector() {
        return other;
    }

    public JoinType getJoinType() {
        return joinType;
    }
}
