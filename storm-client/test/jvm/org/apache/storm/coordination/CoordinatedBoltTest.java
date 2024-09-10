package org.apache.storm.coordination;

import org.apache.storm.Constants;
import org.apache.storm.task.GeneralTopologyContext;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.Config;
import org.apache.storm.topology.OutputFieldsDeclarer;
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

    private final Map<String, Object> configurations;
    private final String srcComp;
    private final String streamId;
    private final List<Object> data;
    private final CoordinatedBolt.SourceArgs sourceArgs;
    private final CoordinatedBolt.IdStreamSpec idStreamSpec;

    public CoordinatedBoltTest(Map<String, Object> configurations, String srcComp, String streamId, List<Object> data, CoordinatedBolt.SourceArgs sourceArgs, CoordinatedBolt.IdStreamSpec idStreamSpec){
        this.configurations = configurations;
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

        Map<String,Object> emptyConfig = new HashMap<>();
        Map<String,Object> configurations = new HashMap<>();
        configurations.put(Config.TOPOLOGY_BOLTS_WINDOW_LENGTH_COUNT, 100);

        return Arrays.asList(new Object[][]{
                {configurations,"prova", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "streamId")},
                {configurations,"prova", "", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "")},
                {configurations,"prova", Constants.COORDINATED_STREAM_ID, data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", Constants.COORDINATED_STREAM_ID)},

                {configurations,"prova", "streamId", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "streamId")},
                {configurations,"prova", "", data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", "")},
                {configurations,"prova", Constants.COORDINATED_STREAM_ID, data1, CoordinatedBolt.SourceArgs.single(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("prova", Constants.COORDINATED_STREAM_ID)},

                {configurations,"", "streamId", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "streamId")},
                {configurations,"", "", data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", "")},
                {configurations,"", Constants.COORDINATED_STREAM_ID, data1, CoordinatedBolt.SourceArgs.all(), CoordinatedBolt.IdStreamSpec.makeDetectSpec("", Constants.COORDINATED_STREAM_ID)},

                {configurations,"","streamId",data1,mock(CoordinatedBolt.SourceArgs.class),CoordinatedBolt.IdStreamSpec.makeDetectSpec(null,null)},
                {configurations,"","",data1,mock(CoordinatedBolt.SourceArgs.class),CoordinatedBolt.IdStreamSpec.makeDetectSpec(null,null)},
                {null,"","",data1,mock(CoordinatedBolt.SourceArgs.class),CoordinatedBolt.IdStreamSpec.makeDetectSpec(null,null)},
                {null,"prova","streamId",data1,mock(CoordinatedBolt.SourceArgs.class),null},
                {null,"prova",Constants.COORDINATED_STREAM_ID,data1,mock(CoordinatedBolt.SourceArgs.class),CoordinatedBolt.IdStreamSpec.makeDetectSpec(null,null)},
                {null,null, "", dataNull, CoordinatedBolt.SourceArgs.single(), null},


                {emptyConfig,"prova","streamId",data1,mock(CoordinatedBolt.SourceArgs.class),null},
                {emptyConfig,"prova",Constants.COORDINATED_STREAM_ID,data1,mock(CoordinatedBolt.SourceArgs.class),CoordinatedBolt.IdStreamSpec.makeDetectSpec(null,null)},
        });

    }

    @Test
    public void testClass() {
        IRichBolt richBolt = mock(IRichBolt.class);

        CoordinatedBolt coordBolt = new CoordinatedBolt(richBolt, this.srcComp, this.sourceArgs, this.idStreamSpec);

        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        coordBolt.prepare(this.configurations, context, collector);

        GeneralTopologyContext gtc = mock(GeneralTopologyContext.class);

        TupleImpl tuple = new TupleImpl(gtc, this.data, this.srcComp, 1, this.streamId);

        coordBolt.execute(tuple);

        if (this.idStreamSpec != null && this.idStreamSpec.id.get_streamId() != null) {
            assertEquals(this.streamId, this.idStreamSpec.id.get_streamId());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("key", 33);
        when(richBolt.getComponentConfiguration()).thenReturn(map);
        Assert.assertEquals(map, coordBolt.getComponentConfiguration());
        coordBolt.cleanup();


    }

    @Test
    public void testWithDifferentConstructor(){

        BoltTimeOut boltTimeOut = mock(BoltTimeOut.class);
        CoordinatedBolt coordBolt = new CoordinatedBolt(boltTimeOut);

        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        Map<String, Object> configurations = new HashMap<>();

        coordBolt.prepare(configurations, context, collector);

        GeneralTopologyContext gtc = mock(GeneralTopologyContext.class);

        TupleImpl tuple = new TupleImpl(gtc, this.data, this.srcComp, 1, this.streamId);

        coordBolt.execute(tuple);

        coordBolt.cleanup();

    }

    @Test
    public void testForExcep(){
        IRichBolt richBolt = mock(IRichBolt.class);
        CoordinatedBolt coordBolt = new CoordinatedBolt(richBolt, this.srcComp, this.sourceArgs, this.idStreamSpec);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);
        GeneralTopologyContext gtc = mock(GeneralTopologyContext.class);

        TupleImpl tuple = new TupleImpl(gtc, this.data, this.srcComp, 1, this.streamId);

        try{
            coordBolt.prepare(this.configurations, null, collector);

        } catch (NullPointerException e) {
            assertFalse(false);
        }

        try{
            coordBolt.prepare(this.configurations, context, collector);
            coordBolt.execute(null);
        } catch (NullPointerException  e) {
            assertFalse(false);
        }

        try{
            coordBolt.prepare(this.configurations, context, null);
            coordBolt.execute(tuple);
        } catch (NullPointerException  e) {
            assertFalse(false);
        }
    }


    private static class BoltTimeOut implements IRichBolt, CoordinatedBolt.TimeoutCallback{

        @Override
        public void timeoutId(Object id) {

        }

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


}