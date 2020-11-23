package org.isel.thesis.impads.storm.low_level.topology.bolts.join;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TimestampExtractor;
import org.apache.storm.windowing.TupleWindow;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObservableJoinBolt<T1, T2, K> extends BaseWindowedBolt {

    private final JoinSelector joinSelectorType;
    private final String fromStream;
    private final KeySelector<T1, K> fromStreamKeySelector;
    private final TupleFieldSelector<T1> fromStreamTupleFieldSelector;
    private final String joinStream;
    private final KeySelector<T2, K> joinStreamKeySelector;
    private final TupleFieldSelector<T2> joinStreamTupleFieldSelector;
    private final JoinType joinType;
    private final JoinFunction<T1, T2, Values> joinFunction;
    private final String[] outputFields;

    private OutputCollector collector;

    public ObservableJoinBolt(JoinSelector joinSelectorType
            , String fromStream
            , KeySelector<T1, K> fromStreamKeySelector
            , TupleFieldSelector<T1> fromStreamTupleFieldSelector
            , String joinStream
            , KeySelector<T2, K> joinStreamKeySelector
            , TupleFieldSelector<T2> joinStreamTupleFieldSelector
            , JoinType joinType
            , JoinFunction<T1, T2, Values> joinFunction
            , String[] outputFields) {
        this.joinSelectorType = joinSelectorType;
        this.fromStream = fromStream;
        this.fromStreamKeySelector = fromStreamKeySelector;
        this.fromStreamTupleFieldSelector = fromStreamTupleFieldSelector;
        this.joinStream = joinStream;
        this.joinStreamKeySelector = joinStreamKeySelector;
        this.joinStreamTupleFieldSelector = joinStreamTupleFieldSelector;
        this.joinType = joinType;
        this.joinFunction = joinFunction;
        this.outputFields = outputFields;
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        List<Tuple> currentWindow = inputWindow.get();
        List<Values> joinResult = hashJoin(currentWindow);

        for (Values result : joinResult) {
            collector.emit(result);
        }
    }

    protected List<Values> hashJoin(List<Tuple> tuples) {
        List<T1> fromInputs = new LinkedList<>();
        List<T2> joinInputs = new LinkedList<>();

        for (Tuple tuple : tuples) {
            String streamId = getStreamSelector(tuple);
            if (!streamId.equals(fromStream)) {
                joinInputs.add(joinStreamTupleFieldSelector.getSelector().apply(tuple));
            }
            else {
                fromInputs.add(fromStreamTupleFieldSelector.getSelector().apply(tuple));
            }
        }

        return doJoin(fromInputs, joinInputs);
    }

    private String getStreamSelector(Tuple tuple) {
        switch (joinSelectorType) {
            case STREAM:
                return tuple.getSourceStreamId();
            case SOURCE:
                return tuple.getSourceComponent();
            default:
                throw new RuntimeException(joinSelectorType + " stream selector type not yet supported");
        }
    }

    protected List<Values> doJoin(List<T1> fromInputs
            , List<T2> joinInputs) {

        switch (joinType) {
            case INNER:
                return doInnerJoin(fromInputs, joinInputs);
            case LEFT:
            case RIGHT:
            case OUTER:
            default:
                throw new RuntimeException("Unsupported join type : " + joinType);
        }
    }

    // inner join - core implementation
    protected List<Values> doInnerJoin(List<T1> fromInputs
            , List<T2> joinInputs) {

        final List<Values> output = new LinkedList<>();

        for (T1 from : fromInputs) {
            for (T2 join : joinInputs) {
                K fromKey = fromStreamKeySelector.getSelector().apply(from);
                K joinKey = joinStreamKeySelector.getSelector().apply(join);

                if (fromKey.equals(joinKey)) {
                    output.add(joinFunction.apply(from, join));
                }
            }
        }

        return output;
    }

    @Override
    public ObservableJoinBolt withWindow(Count windowLength, Count slidingInterval) {
        return (ObservableJoinBolt) super.withWindow(windowLength, slidingInterval);
    }

    @Override
    public ObservableJoinBolt withWindow(Count windowLength, Duration slidingInterval) {
        return (ObservableJoinBolt) super.withWindow(windowLength, slidingInterval);
    }

    @Override
    public ObservableJoinBolt withWindow(Duration windowLength, Count slidingInterval) {
        return (ObservableJoinBolt) super.withWindow(windowLength, slidingInterval);
    }

    @Override
    public ObservableJoinBolt withWindow(Duration windowLength, Duration slidingInterval) {
        return (ObservableJoinBolt) super.withWindow(windowLength, slidingInterval);
    }

    @Override
    public ObservableJoinBolt withWindow(Count windowLength) {
        return (ObservableJoinBolt) super.withWindow(windowLength);
    }

    @Override
    public ObservableJoinBolt withWindow(Duration windowLength) {
        return (ObservableJoinBolt) super.withWindow(windowLength);
    }

    @Override
    public ObservableJoinBolt withTumblingWindow(Count count) {
        return (ObservableJoinBolt) super.withTumblingWindow(count);
    }

    @Override
    public ObservableJoinBolt withTumblingWindow(Duration duration) {
        return (ObservableJoinBolt) super.withTumblingWindow(duration);
    }

    @Override
    public ObservableJoinBolt withTimestampField(String fieldName) {
        return (ObservableJoinBolt) super.withTimestampField(fieldName);
    }

    @Override
    public ObservableJoinBolt withTimestampExtractor(TimestampExtractor timestampExtractor) {
        return (ObservableJoinBolt) super.withTimestampExtractor(timestampExtractor);
    }

    @Override
    public ObservableJoinBolt withLateTupleStream(String streamId) {
        return (ObservableJoinBolt) super.withLateTupleStream(streamId);
    }

    @Override
    public ObservableJoinBolt withLag(Duration duration) {
        return (ObservableJoinBolt) super.withLag(duration);
    }

    @Override
    public ObservableJoinBolt withWatermarkInterval(Duration interval) {
        return (ObservableJoinBolt) super.withWatermarkInterval(interval);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }

    public static class JoinBuilder<T1, T2, K> {

        private final JoinSelector joinSelectorType;
        private final String fromStream;
        private final KeySelector<T1, K> fromStreamKeySelector;
        private final TupleFieldSelector<T1> fromStreamTupleFieldSelector;

        private String joinStream;
        private KeySelector<T2, K> joinStreamKeySelector;
        private TupleFieldSelector<T2> joinStreamTupleFieldSelector;
        private JoinType joinType;
        private JoinFunction<T1, T2, Values> joinFunction;
        private String[] outputFields;

        private JoinBuilder(JoinSelector joinSelectorType
                , String fromStream
                , KeySelector<T1, K> fromStreamKeySelector
                , TupleFieldSelector<T1> fromStreamTupleFieldSelector) {
            this.joinSelectorType = joinSelectorType;
            this.fromStream = fromStream;
            this.fromStreamKeySelector = fromStreamKeySelector;
            this.fromStreamTupleFieldSelector = fromStreamTupleFieldSelector;
        }

        public static <T1, T2, K> JoinBuilder<T1, T2, K> from(String fromStream
                , KeySelector<T1, K> fromStreamKeySelector
                , TupleFieldSelector<T1> fromStreamTupleFieldSelector) {
            return new JoinBuilder<>(JoinSelector.SOURCE, fromStream, fromStreamKeySelector, fromStreamTupleFieldSelector);
        }

        public JoinBuilder<T1, T2, K> join(String joinStream
                , KeySelector<T2, K> joinStreamKeySelector
                , TupleFieldSelector<T2> joinStreamTupleFieldSelector) {
            this.joinStream = joinStream;
            this.joinStreamKeySelector = joinStreamKeySelector;
            this.joinStreamTupleFieldSelector = joinStreamTupleFieldSelector;
            this.joinType = JoinType.INNER;
            return this;
        }

        public JoinBuilder<T1, T2, K> apply(JoinFunction<T1, T2, Values> joinFunction) {
            this.joinFunction = joinFunction;
            return this;
        }

        public JoinBuilder<T1, T2, K> outputFields(String... outputFields) {
            this.outputFields = outputFields;
            return this;
        }

        public ObservableJoinBolt<T1, T2, K> build() {
            return new ObservableJoinBolt<>(joinSelectorType
                    , fromStream
                    , fromStreamKeySelector
                    , fromStreamTupleFieldSelector
                    , joinStream
                    , joinStreamKeySelector
                    , joinStreamTupleFieldSelector
                    , joinType
                    , joinFunction
                    , outputFields);
        }
    }
}
