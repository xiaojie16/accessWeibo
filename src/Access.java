import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author  by kissx on 2017/1/12.
 */
public class Access {

    private final static String baseUrl = "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F";
    private static int number = 1;

    private WebClient webClient;
    private HtmlPage homePage;
    private int totalSend;

    public Access(int totalSend) {
        webClient = new WebClient();
        this.totalSend = totalSend;
    }

    public void login(String userName, String password) throws IOException {
        //关闭错误警告
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setTimeout(1000000);
        HtmlPage loginPage = webClient.getPage(baseUrl);
        //等待页面加载
        webClient.waitForBackgroundJavaScript(1000);
        //获取输入帐号的控件
        HtmlInput inputLogin = (HtmlInput) loginPage.getElementById("loginName");
        inputLogin.setValueAttribute(userName);
        HtmlInput inputPwd = (HtmlInput) loginPage.getElementById("loginPassword");
        inputPwd.setValueAttribute(password);
        HtmlAnchor anchorLogin = loginPage.getAnchorByText("登录");
        homePage = anchorLogin.click();
        webClient.waitForBackgroundJavaScript(1000);
    }

    /**
     * 发微博
     * @param content 微博内容
     * @throws IOException 异常
     */
    private void send(String content) throws IOException {
        HtmlAnchor anchorWrite = homePage.getAnchorByHref("/mblog");
        HtmlPage writePage = anchorWrite.click();
        webClient.waitForBackgroundJavaScript(1000);
        HtmlTextArea textArea = writePage.getHtmlElementById("txt-publisher");
        textArea.setTextContent(content);
        HtmlAnchor anchorSend = writePage.getAnchorByText("发送");
        //改变发送按钮的属性,不能无法发送
        anchorSend.setAttribute("class","fr txt-link");  //注意这里
        anchorSend.click();

        //测试显示发微博的时间
        System.out.println("当前时间为：" + System.currentTimeMillis());

        webClient.waitForBackgroundJavaScript(1000);
    }

    /**
     *  定期发送微博
     * @param cycTime 周期
     */
    public void regularSend(int cycTime){
        //方法一
        new Thread(() -> {
            String absolutePath = new File("").getAbsolutePath();
            try(BufferedReader bfReader = new BufferedReader(new FileReader(absolutePath + "\\contents.txt"))){
                for (int i = 0; i < totalSend; i++) {
                    String temp;
                    int count = 1;
                    while ((temp = bfReader.readLine()) != null) {
                        if (count == number) {
                            send(temp);
                            break;
                        }
                        ++count;
                    }
                    Thread.sleep(cycTime);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void close(){
        if (webClient != null) {
            webClient.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String userName = "18635792910";        //此处输入你的账号
        String password = "...";        //此处输入你的密码
        Access access = new Access(5);
        access.login(userName,password);
//        access.send("测试！！！");
        access.regularSend(5000);
//        access.close();               //注意关闭的时间
    }

/*
    //测试点击新浪新闻里面的体育
    private static void chick()throws Exception{
        //关闭 httpunit 不能正确运行 JavaScript 的异常信息
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        //设置超时
        webClient.getOptions().setTimeout(50000);
        //启动客户端重定向
//        webClient.getOptions().setRedirectEnabled(true);
        //设置 Ajax
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置cookie
//        webClient.getCookieManager().setCookiesEnabled(true);
        HtmlPage page = webClient.getPage("http://www.sina.com.cn/");
        //获取登录元素
        HtmlAnchor anchor = page.getAnchorByHref("http://sports.sina.com.cn/");
        //获取登录后的页面
        HtmlPage myPage = anchor.click();
        System.out.println(myPage.asText());
        webClient.close();
    }
    //*/

     /*
    //TODO 由于不能响应点击登录事件,所以这里流产,改用 POST/GET 请求 --------------- 这里因为此处的 JavaScript 太复杂
    private static void login(String userName,String password)throws Exception{
        //关闭 httpunit 不能正确运行 JavaScript 的异常信息
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        //设置超时
        webClient.getOptions().setTimeout(50000);
        //启动客户端重定向
        webClient.getOptions().setRedirectEnabled(true);
        //设置 Ajax
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置cookie
//        webClient.getCookieManager().setCookiesEnabled(true);
        HtmlPage page = webClient.getPage("http://weibo.com/login.php/");
        //获取账号 input
        webClient.waitForBackgroundJavaScript(1000);
        HtmlInput inputUser = (HtmlInput) page.getElementById("loginname");
        inputUser.setValueAttribute(userName);
        //获取密码 input
        HtmlInput inputPass = page.getElementByName("password");
        inputPass.setValueAttribute(password);
        //获取登录元素(此处登录元素为 a标签)
        HtmlAnchor anchorLogin = page.getAnchorByText("登录");
//        获取登录后的页面
        HtmlPage myPage = anchorLogin.click();
        webClient.waitForBackgroundJavaScript(1000);    //遇到的问题-----需要一段时间来运行 javascript
        System.out.println(myPage.asText());
        webClient.close();
    }
    //*/

}
