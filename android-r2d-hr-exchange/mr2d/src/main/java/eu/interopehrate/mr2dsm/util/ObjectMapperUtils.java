package eu.interopehrate.mr2dsm.util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.beanutils.PropertyUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class ObjectMapperUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertToUrlEncoded(Object obj) {
        Map<String, String> map = objectMapper.convertValue(obj, Map.class);
        return map.keySet().stream()

                .map(key -> {
                    try {
                        Class typeClass = PropertyUtils.getPropertyType(obj, key);
                        String type = typeClass.getSimpleName();
                        String value = null;
                        if (type.equals("String")) {
                            value = map.get(key);
                        } else if (typeClass.isPrimitive()) {
                            value = String.valueOf(map.get(key));
                        }

                        return value != null && value.length() > 0
                                ? key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                                : null;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException(); // ???
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(joining("&"));
    }
}
