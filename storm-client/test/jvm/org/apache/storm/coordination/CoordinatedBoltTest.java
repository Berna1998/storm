package org.apache.storm.coordination;

import org.apache.storm.Constants;
import org.apache.storm.generated.Grouping;
import org.apache.storm.task.GeneralTopologyContext;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.Config;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.TupleImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(value = Parameterized.class)
public class CoordinatedBoltTest {

    private final Integer timeO;
    private final String srcComp;
    private final String streamId;
    private final List<Object> data;
    private final CoordinatedBolt.SourceArgs sourceArgs;
    private final CoordinatedBolt.IdStreamSpec idStreamSpec;

    public CoordinatedBoltTest(Integer timeO, String srcComp, String streamId, List<Object> data, CoordinatedBolt.SourceArgs sourceArgs, CoordinatedBolt.IdStreamSpec idStreamSpec){
        this.timeO = timeO;
        this.srcComp = srcComp;
        this.streamId = streamId;
        this.data = data;
        this.sourceArgs = sourceArgs;
        this.idStreamSpec = idStreamSpec;
    }

    @Parameterized.Parameters
    public static Collection getCoordinatedBolt() {

        List<Object> data1 = new ArrayList<>();
        data1.add("data");
        data1.add(33);

        List<Object> dataNull = new ArrayList<>();
        dataNull.add(null);
        dataNull.add(null);

        return Arrays.asList(new Object[][]{
                {1,"prova", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "streamId")},
                {0,"prova", "", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "")},
                {-1,"prova", Constants.COORDINATED_STREAM_ID, data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", Constants.COORDINATED_STREAM_ID)},

                {-1,"", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},
                {1,"", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},
                {0,"", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},

                //Altre con single()
                {1,"prova", "streamId", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "streamId")},
                {0,"prova", "", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "")},
                {-1,"prova", Constants.COORDINATED_STREAM_ID, data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", Constants.COORDINATED_STREAM_ID)},

                {-1,"", "streamId", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},
                {1,"", "streamId", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},
                {0,"", "streamId", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},


                //coverage execute
                {1, "prova", "streamId", data1, CoordinatedBolt.SourceArgs.single(), null},
                {1,"prova", Constants.COORDINATED_STREAM_ID,data1,CoordinatedBolt.SourceArgs.all(),null},


        });


    }

    @Test
    public void testClass() {

        MyBolt bolt2 = new MyBolt();

        CoordinatedBolt coordBolt = new CoordinatedBolt(bolt2, this.srcComp, this.sourceArgs, this.idStreamSpec);

        TopologyContext context = mock(TopologyContext.class);

        when(context.maxTopologyMessageTimeout()).thenAnswer(invocation -> this.timeO);

        Map<String, Map<String, Grouping>> map = new HashMap<>();
        Map<String, Grouping> group = new HashMap<>();
        group.put("groupingMap", Grouping.custom_serialized("groupingMap".getBytes()));
        map.put("groupingMap", group);

        when(context.getThisTargets()).thenAnswer(invocation -> map);

        List<Integer> listInt = new ArrayList<>();
        listInt.add(1);
        listInt.add(2);
        listInt.add(3);
        when(context.getComponentTasks(anyString())).thenAnswer(invocation -> listInt);

        OutputCollector collector = mock(OutputCollector.class);

        Map<String,Object> configurations = new HashMap<>();
        coordBolt.prepare(configurations, context, collector);


        TopologyBuilder builder = new TopologyBuilder();
        GeneralTopologyContext gtc = new GeneralTopologyContext(builder.createTopology(),
                new Config(), new HashMap<>(), new HashMap<>(), new HashMap<>(), "") {
            @Override
            public Fields getComponentOutputFields(String componentId, String streamId) {
                return new Fields("key", "value");
            }

        };

        TupleImpl tuple = new TupleImpl(gtc, this.data, this.srcComp, 1, this.streamId);

        coordBolt.execute(tuple);

        if (this.idStreamSpec != null && this.idStreamSpec.id.get_streamId() != null) {
            assertEquals(this.streamId, this.idStreamSpec.id.get_streamId());
        }

        coordBolt.cleanup();


    }

    @Test
    public void testWithDifferentConstructor(){

        BoltTimeOut bolt2 = new BoltTimeOut();
        CoordinatedBolt coordBolt = new CoordinatedBolt(bolt2);


        TopologyContext context = mock(TopologyContext.class);

        when(context.maxTopologyMessageTimeout()).thenAnswer(invocation -> null);

        Map<String, Map<String, Grouping>> map = new HashMap<>();
        Map<String, Grouping> group = new HashMap<>();
        group.put("groupingMap", Grouping.custom_serialized("groupingMap".getBytes()));
        map.put("groupingMap", group);

        when(context.getThisTargets()).thenAnswer(invocation -> map);

        List<Integer> listInt = new ArrayList<>();
        listInt.add(1);
        listInt.add(2);
        listInt.add(3);
        when(context.getComponentTasks(anyString())).thenAnswer(invocation -> listInt);

        OutputCollector collector = mock(OutputCollector.class);

        Map<String, Object> configurations = new HashMap<>();

        coordBolt.prepare(configurations, context, collector);


        TopologyBuilder builder = new TopologyBuilder();
        GeneralTopologyContext gtc = new GeneralTopologyContext(builder.createTopology(),
                new Config(), new HashMap<>(), new HashMap<>(), new HashMap<>(), "") {
            @Override
            public Fields getComponentOutputFields(String componentId, String streamId) {
                return new Fields("key", "value");
            }

        };

        TupleImpl tuple = new TupleImpl(gtc, this.data, this.srcComp, 1, this.streamId);

        coordBolt.execute(tuple);

        coordBolt.cleanup();

    }

    private static class MyBolt implements IRichBolt{

        @Override
        public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {

        }

        @Override
        public void execute(Tuple input) {

        }

        @Override
        public void cleanup() {

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            return Collections.emptyMap();
        }

    }

    private static class BoltTimeOut extends MyBolt implements  CoordinatedBolt.TimeoutCallback{

        @Override
        public void timeoutId(Object id) {

        }
    }



}