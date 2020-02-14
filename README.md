
# 김서기
![로고](https://user-images.githubusercontent.com/37135317/74507850-f1b69f00-4f40-11ea-9781-eef2f5d497b6.png)

### 사용한 라이브러리
- network moodule : loopj android-async-http library
```
public class Network {
    public static String BASE_URL = "http://--.--.---.---";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Activity act, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if(Useful.isNetworkConnected(act) == false){
            Useful.showAlertDialog(act, "알림", "네트워크에 연결되어 있지 않습니다.\n네트워크 연결 후 다시 시도해 주세요.");
            return;
        }
        client.get(BASE_URL + url, params, responseHandler);
    }

    public static void post(Activity act, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if(Useful.isNetworkConnected(act) == false){
            Useful.showAlertDialog(act, "알림", "네트워크에 연결되어 있지 않습니다.\n네트워크 연결 후 다시 시도해 주세요.");
            return;
        }
        if(params!=null){
            String countryCode = Locale.getDefault().getCountry();
            params.put("country_code", countryCode);
            params.put("order_device", "pos");
        }
        client.setURLEncodingEnabled(false);
        client.post(BASE_URL + url, params, responseHandler);
    }
}
```
- parsing json : GSON
- tag View : Cutta:TagView:1.3


### ScreenShot
![image1](https://user-images.githubusercontent.com/37135317/74507938-32aeb380-4f41-11ea-93cc-335ede1a5f16.png)
![image2](https://user-images.githubusercontent.com/37135317/74507962-40fccf80-4f41-11ea-86c8-d54238d171d3.png)
### Features
 - Analyze audio file and return script 
 - Analyze script file and shows you keyword and information(appointment, reservation) about script.
 - Analyze script file and show Summary of text
 
 ### Permissions
 - Full Network Access.
 - View Network Connections.
 - Read and Write access to external and internal Storage.
 - Read Phone Call state and Record Audio.
 - Read Audio File.


