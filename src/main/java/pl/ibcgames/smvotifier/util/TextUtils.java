package pl.ibcgames.smvotifier.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class TextUtils {
    public static Text message(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }
}
