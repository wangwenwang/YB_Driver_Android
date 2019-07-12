package com.example.administrator.ybdriver.canstants;

import android.graphics.Color;
import android.os.Environment;

/**
 * Created by Administrator on 2016/6/1.
 */
public class Constants {
    /**
     * 错误信息保存路径
     */
    public static final String LOG_SAVE_PATH = Environment
            .getExternalStorageDirectory().toString() + "/com.kdy/";

    /**
     * 版本更新
     */
    public static final String VERSINO_UPDATE_ACTION = "com.kdy.VersionUpdateAction";
    /**
     *@auther: Tom
     *created at 2016/6/2 13:57
     *app基本信息数据
     */
    public static final String BasicInfo="dataBases.BasicInfo";

    public static final String IsUsedApp="isusedapp";

    public static final String UserName="userName";
    public static final String UserPWD="userPwd";
    /**
     *  发起定位请求的间隔时间
     */
    public static int scanSpan = 1000 * 60 * 10;
    //2016.07.18 测试
    //public  static  int scanSpan=1000*60*1;
    public class URL {

        //        public static final String Base_Url = "http://192.168.11.19/api/";
        //		public static final String Load_Url = "http://192.168.11.19/";
     //   public static final String Load_Url = "http://oms.kaidongyuan.com:8088/";
        public static final String Load_Url = "http://218.17.181.244:8081/";
        public static final String Base_Url0 = "http://oms.kaidongyuan.com:8088/api/";
        public static final String Base_Url="http://218.17.181.244:8081/api/";
       //2016.7.19以前测试的怡宝项目基础接口
       // public static final String Base_Url = "http://oms.kaidongyuan.com:8081/api/";
        public static final String Login = Base_Url + "Login";//登录
        public static final String Register=Base_Url+"YibRegister";//注册
        public static final String ModifyPassword = Base_Url + "modifyPassword";//修改密码
       // public static final String GetPartyList = Base_Url + "GetPartyList";//获取客户列表
       // public static final String GetAddress = Base_Url + "GetAddress";//获取地址列表
       // public static final String GetProductList = Base_Url + "GetProductList";//获取产品列表
       // public static final String SubmitOrder = Base_Url + "SubmitOrder";//最终提交订单	需要传客户地址的 IDX，产品的 IDX
       // public static final String GetOrderList = Base_Url + "GetOrderList";//获取订单列表
       // public static final String GetOrderDetail = Base_Url + "GetOrderDetail";//获取订单详情


       // public static final String SubmitOrder1 = Base_Url + "SubmitOrder1";//提交获取促销信息
      //  public static final String ConfirmOrder = Base_Url + "ConfirmOrder";//最终提交订单

       // public static final String GetPaymentType = Base_Url + "GetPaymentType";//获取付款方式 post strLicense  过来就行了
        // 提交订单时 把 KEY 值 送过来

       // public static final String GetPartySalePolicy = Base_Url + "GetPartySalePolicy";
        //		public static final String Information = Base_Url + "Information";
//        public static final String Information = "http://oms.kaidongyuan.com:8088/api/" + "Information";
//        public static final String Information = "http://192.168.11.13/api/" + "Information";
//        public static final String GetNewDetail = "http://192.168.11.13/api/" + "GetNewDetail";
        public static final String Information = Base_Url + "Information";
        public static final String GetNewDetail = Base_Url + "GetNewDetail";
        // GetNewDetail

        // 获取定位轨迹：GetPosition
        // 参数strPhone, strLicense
     //   public static final String GetPosition = "http://192.168.11.13/api/" + "GetPosition";


        // 开启定位：AddPosition
        // 参数 strUserId， strStatus， strPosition， strLicense
        /**
         *
         // <param name="userId">用户ID</param>
         /// <param name="status">状态</param>
         /// <param name="positionlng">经度</param>
         /// <param name="positionlat">纬度</param>
         Position(string userId, string status, string positionlng, string positionlat)
         */
//        public static final String UploadPosition = "http://192.168.11.13/api/" + "UploadPosition";
       // public static final String UploadPosition = Base_Url + "UploadPosition";


        /**
         * strUserIdx
         * cordinateX
         * cordinateY
         * address
         * strLicense
         */
//        public static final String CurrentLocaltion = Test_Url + "CurrentLocaltion";
        public static final String CurrentLocaltion = Base_Url + "CurrentLocaltion";
        public static final String CurrentLocationList = Base_Url + "CurrentLocaltionList";


        /**
         * 获取报表
         * string strUserId = Request["strUserId"].ToString();用户ID
         * string strLicense = Request["strLicense"].ToString();strLicense
         */
//        public static final String CustomerCount = "http://192.168.11.13/api/" + "CustomerCount";
     //   public static final String CustomerCount = Base_Url + "CustomerCount";
        //        public static final String ProductCount = "http://192.168.11.13/api/" + "ProductCount";
      //  public static final String ProductCount = Base_Url + "ProductCount";

        /**
         * 获取物流信息列表
         * strOrderId
         */
      //  public static final String GetOrderTmsList = Base_Url + "GetOrderTmsList";
//        public static final String GetOrderTmsList = Test_Url + "GetOrderTmsList";

        /**
         * 获取物流信息详情
         */
//        public static final String GetOrderTmsInfo = Test_Url + "GetOrderTmsInfo";
        public static final String GetOrderTmsInfo = Base_Url + "GetOrderTmsInfo";

        /**
         * 获取司机订单列表
         */
//        public static final String GetDriverOrderList = Test_Url + "GetDriverOrderList";
        public static final String GetDriverOrderList = Base_Url + "GetDriverOrderList";
        public static final String GetDriverOrderDate=Base_Url+"GetDriverOrderDate";
        public static final String GetDriverOrderListNumber=Base_Url+"GetDriverOrderListNumber";//通过车牌号查询已交付订单
        /**
         * 交付
         * strOrderIdx  strLicense
         */
//        public static final String DriverPay = Test_Url + "DriverPay";
        public static final String DriverPay = Base_Url + "DriverPay1";
        public static final String IsPayedOrder=Base_Url0+"IsPayedOrder";

        /**
         * 获取订单位置信息
         * strOrderIdx  strLicense
         */
//      public static final String GetLocaltion = Test_Url + "GetLocaltion";
        public static final String GetLocaltion = Base_Url + "GetLocaltion";

        /**
         * 获取最新版本 app 信息
         */
        public static final String CheckVersion = Base_Url + "GetVersion";
        /**
         * 获取货物轨迹信息
         */
       // public static final String OrderTrackCheck = Base_Url + "GetLocaltionForOrdNo";
        /**
         * 获取电子签名和交货现场图片
         */
        public static final String GETAUTOGRAPH = Base_Url + "GetAutograph";
        /**
         *@auther: Tom
         *created at 2016/8/25 14:22
         *获取发货数图表数据信息
         */
        public static final String GetCestbonCount=Base_Url+"GetCestbonCount";
        public static final String GetDateCestbonCount=Base_Url+"GetDateCestbonCount";//+时间段筛选功能
        /**
         *@auther: Tom
         *created at 2018/5/31 15:10
         *
         */
        public static final String GetCestbonFleetCount=Base_Url+"GetCestbonFleetCount";
        public static final String GetDateCestbonFleetCount=Base_Url+"GetDateCestbonFleetCount";//+时间段筛选功能
    }
    public static final int[] MPBAR_COLORS = {
            Color.rgb(255, 105, 180),  Color.rgb(240, 230, 140), Color.rgb(60, 179, 113),
            Color.rgb(255, 69, 0), Color.rgb(255, 20, 147), Color.rgb(255, 165, 0),
            Color.rgb(0, 191, 255), Color.rgb(210, 105, 30), Color.rgb(148, 0, 211),
            Color.rgb(30, 144, 225), Color.rgb(123, 104, 238),
            Color.rgb(255, 105, 180),  Color.rgb(240, 230, 140), Color.rgb(60, 179, 113),
            Color.rgb(255, 69, 0), Color.rgb(255, 20, 147), Color.rgb(255, 165, 0),
            Color.rgb(0, 191, 255), Color.rgb(210, 105, 30), Color.rgb(148, 0, 211),
            Color.rgb(30, 144, 225), Color.rgb(123, 104, 238)
    };

    /**
     * 20180530 扫描二维码基本信息常量
     */
    public static final int RESULT_CODE_QR_SCAN = 0xA1;
    public static  final String INTENT_EXTRA_KEY_QR_SCAN="qr_scan_result";

}
