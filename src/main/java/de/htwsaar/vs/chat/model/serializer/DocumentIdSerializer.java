package de.htwsaar.vs.chat.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.htwsaar.vs.chat.model.DocumentWithId;

import java.io.IOException;

/**
 * Jackson serializer used on fields implementing {@link DocumentWithId}.
 * <p>
 * It writes the id of a given document to JSON.
 *
 * @author Arthur Kelsch
 */
public class DocumentIdSerializer extends StdSerializer<DocumentWithId> {

    public DocumentIdSerializer() {
        super(DocumentWithId.class);
    }

    @Override
    public void serialize(DocumentWithId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getId());
    }
}
