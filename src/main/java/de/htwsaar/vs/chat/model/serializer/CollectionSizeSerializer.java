package de.htwsaar.vs.chat.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Jackson serializer used on fields implementing {@link Collection}.
 * <p>
 * It writes the size of a given collection to JSON.
 *
 * @author Arthur Kelsch
 */
public class CollectionSizeSerializer extends StdSerializer<Collection<?>> {

    public CollectionSizeSerializer() {
        super(TypeFactory.defaultInstance().constructType(Collection.class));
    }

    @Override
    public void serialize(Collection<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.size());
    }
}
