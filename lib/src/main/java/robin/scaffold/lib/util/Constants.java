package robin.scaffold.lib.util;

public class Constants {
    public interface RequestCode {
        int ACTIVITY_REQUEST_CODE_OPEN_CAMREA = 0x01;
        int ACTIVITY_REQUEST_CODE_CHOOSE_FILE = 0x02;
    }

    public interface Net {
        String HTTP = "http";
        String HTTPS = "https";

        int REPONSE_CODE_UNKNOW = 1;
        int REPONSE_CODE_OK = 200;
        int REPONSE_CODE_BAD_REQUEST = 400;
        int REPONSE_CODE_UNAUTHORIZATION = 401;
        int REPONSE_CODE_NOT_FOUND = 404;
        int REPONSE_CODE_BAD_SERVER_ERR = 500;
        int REPONSE_CODE_BAD_SERVER_UNAVALIABLE = 503;

        String REQUEST_METHOD_GET = "GET";
        String REQUEST_METHOD_POST = "POST";
    }
}