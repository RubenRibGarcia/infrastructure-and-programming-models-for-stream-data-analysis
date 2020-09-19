package org.isel.thesis.impads.flink.fasterxml.jackson.deserializers;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.flink.topology.sourcemodel.ipma.IpmaStationValueImpl;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStations;

import java.io.IOException;

public class IpmaStationValueDeserializer extends StdDeserializer<IpmaStationValue<Float>> {

    public IpmaStationValueDeserializer() {
        this(null);
    }

    public IpmaStationValueDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IpmaStationValue<Float> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Float value = node.get("value").floatValue();
        IpmaStations ipmaStations = IpmaStations.valueOf(node.get("station").asText());

        final IpmaStationValueImpl<Float> rvalue = new IpmaStationValueImpl<Float>();
        rvalue.setValue(value);
        rvalue.setStation(ipmaStations);

        return rvalue;
    }
}
