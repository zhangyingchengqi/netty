package pro5_decoder_encoder.ex4_jsonSerialize;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @program: netty
 * @description: 用于获取 ObjectMapper 实例
 * @author: zy
 * @create: 2025-03-23 10:34
 */
public class JacksonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return MAPPER;
    }
}
