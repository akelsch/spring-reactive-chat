package de.htwsaar.vs.chat.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.htwsaar.vs.chat.model.BaseDocument;

import java.io.IOException;

/**
 * Jackson serializer used on fields extending {@link BaseDocument}.
 * <p>
 * It writes the id of a given document to JSON.
 *
 * @author Arthur Kelsch
 */
public class DocumentIdSerializer extends StdSerializer<BaseDocument> {

    public DocumentIdSerializer() {
        super(BaseDocument.class);
    }

    @Override
    public void serialize(BaseDocument value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
