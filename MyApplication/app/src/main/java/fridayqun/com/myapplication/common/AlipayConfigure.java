package fridayqun.com.myapplication.common;

/**
 * Created by lenovo on 2016/5/30.
 */
public class AlipayConfigure {

    // 开发者应用私钥。java配置PKCS8格式，PHP/.Net语言配置rsa_private_key.pem文件中原始私钥。
    public static final String RSA_RRIVATE_KEY =
                    "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANm8g+Mn5eUBZ0Qk"+
                    "QPHXh2LkUv7lf48Kg1fPl1O3DUEuXCXAA1z+AvFnWhWRnAV/8je7atCCfn4f8N1/"+
                    "dTJgPUiBbthg2KPX8FtJmmBhANtnI9eZ0Zbg4n/uf0en9JsSh6n01vrWqJrDT81V"+
                    "g5RNl1UEgojMYHNt9sJrGcWjopPrAgMBAAECgYAIHPglHLV6RQUeCuPInylSTAOz"+
                    "r8WDBPdD7F5NJkL+LMYOOgtmCaj+acV3jEduBQh3fypJvrML7BDDkB5D1EY0PdUe"+
                    "loputRojzLFFxTBbHsjSCLrdPh5UOgpHQpEU3pkXNP6vh2LzZ5/zs4WA9bIjlljG"+
                    "mdfN/9zdXWRnLgM8oQJBAPFNMjRWjrL7hFZfct6DTGcJRfUZ5vngQZVx8IeMYZ/K"+
                    "kb1wLLKLcKn8WcDnef1q0AoVNkqPZDnszZ4VMbK8ZxMCQQDm/9leF3E8WZf2rp0o"+
                    "81uDXBumTPiNhuy/8bzvT1Vd/G8CKHrllW3Cl6m6pHBcAnO8HmOie1LAAQWMC9su"+
                    "6oLJAkAUWoaZk5OF1WFcR8DQtBKFvmqAOicZfKkLigjX5id16whQCl460Tg3nmmT"+
                    "s/K+RXw23YMu1tmFaUwOwYrnoKyxAkEAjdL1dPR1nHTxfQ32BcdsHzl07GSDkfkN"+
                    "8EcOFeuiYQL0cOHDmqNk8T1GgwYkygjjX9NUntSSmLnH2xUIbESlKQJBAOeQK5Dy"+
                    "/ypB1ABdOM2X6cpW5U8c+aKLFlBO5p+P5XDifCe1/u7OBUYujC+WDex12ZI1O7aZ"+
                    "+wVjBjAojKISeIE=";;

    // 接口请求网关。当面付支付、查询、退款、撤销接口中为固定值
    public static final String URL = "https://openapi.alipay.com/gateway.do";

    // 商户应用APPID，只要您的应用中包含当面付接口且是开通状态，就可以用此应用对应的appid。开发者可登录开放平台-管理中心-对应应用中查看
    public static final String APPID = "2015070800159962";
    // 编码字符集。默认 utf-8
    public static final String CHARSET = "utf-8";
    // 返回格式。默认json
    public static final String FORMAT = "json";
    // 支付宝公钥，用于获取同步返回信息后进行验证，验证是否是支付宝发送的信息。
    public static final String ALIPAY_PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkr" +
            "IvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsra" +
            "prwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUr" +
            "CmZYI/FCEa3/cNMW0QIDAQAB";

}
