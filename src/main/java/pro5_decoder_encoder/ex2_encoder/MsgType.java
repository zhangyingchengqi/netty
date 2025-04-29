package pro5_decoder_encoder.ex2_encoder;

public enum MsgType {
    EMGW_LOGIN_REQ((byte) 0x00),  //请求
    EMGW_LOGIN_RES((byte) 0x01);  //响应

    private byte value;

    public byte getValue() {
        return value;
    }

    private MsgType(byte value) {
        this.value = value;
    }
}
