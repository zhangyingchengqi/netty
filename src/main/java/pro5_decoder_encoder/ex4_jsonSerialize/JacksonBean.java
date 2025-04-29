package pro5_decoder_encoder.ex4_jsonSerialize;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JacksonBean {
    private int age;
    private String name;
    private List<String> sons;
    private Map<String, String> addrs;


}
