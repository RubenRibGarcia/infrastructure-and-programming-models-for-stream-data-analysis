package org.isel.thesis.impads.kafka.stream.fasterxml.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStationValue;
import org.isel.thesis.impads.giragen.datamodel.api.ipma.api.IpmaStations;
import org.isel.thesis.impads.kafka.stream.topology.sourcemodel.ipma.IpmaStationValueImpl;

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
