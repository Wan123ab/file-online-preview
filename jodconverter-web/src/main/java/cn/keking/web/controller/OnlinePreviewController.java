package cn.keking.web.controller;

import cn.keking.service.FilePreview;
import cn.keking.service.FilePreviewFactory;

import cn.keking.service.cache.CacheService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author yudian-it
 */
@Controller
public class OnlinePreviewController {

    @Autowired
    private FilePreviewFactory previewFactory;

    @Value("${file.dir}")
    private String fileDir;

    /**
     * CacheService接口有3个实现，但是这里注入并不会报错，因为实现类上标注了下面注解
     * @ConditionalOnExpression
     * 由于配置的原因{@link application.properties}，默认注入CacheServiceRocksDBImpl
     */
    @Resource
    private CacheService cacheService;

    /**
     * @param url
     * @param model
     * @return
     */
    @RequestMapping(value = "onlinePreview", method = RequestMethod.GET)
    public String onlinePreview(String url, Model model, HttpServletRequest req) {
        /**
         * 如果是md文件，使用Editor.md进行渲染
         */
        String fileSuffix = url.substring(url.lastIndexOf(".") + 1);
        if("md".equalsIgnoreCase(fileSuffix)){
            return onlinePreviewMD(url, model);
        }

        req.setAttribute("fileKey", req.getParameter("fileKey"));
        model.addAttribute("officePreviewType", req.getParameter("officePreviewType"));
        model.addAttribute("originUrl",req.getRequestURL().toString());
        /**
         * url  http://localhost:8012/demo/557422 精通Spring 4.x 企业应用开发实战[www.rejoiceblog.com].pdf
         */
        FilePreview filePreview = previewFactory.get(url);
        return filePreview.filePreviewHandle(url, model);
    }

    /**
     * md文件在线预览
     * @param url
     * @param model
     * @return
     */
    public String onlinePreviewMD(String url, Model model) {
        String filePath = url.substring(url.indexOf("demo"));
        String fileName = url.substring(url.indexOf("demo") + "demo".length() + 1);
        filePath = fileDir + filePath;

        String readFileToString = null;
        try {
            /**
             * 获取md文件的内容
             */
            readFileToString = FileUtils.readFileToString(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("content", readFileToString);
        model.addAttribute("fileName", fileName);

        return "md";
    }


    /**
     * 多图片切换预览
     *
     * @param model
     * @param req
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "picturesPreview", method = RequestMethod.GET)
    public String picturesPreview(String urls, String currentUrl, Model model, HttpServletRequest req) throws UnsupportedEncodingException {
        // 路径转码
        String decodedUrl = URLDecoder.decode(urls, "utf-8");
        String decodedCurrentUrl = URLDecoder.decode(currentUrl, "utf-8");
        // 抽取文件并返回文件列表
        String[] imgs = decodedUrl.split("\\|");
        List imgurls = Arrays.asList(imgs);
        model.addAttribute("imgurls", imgurls);
        model.addAttribute("currentUrl",decodedCurrentUrl);
        return "picture";
    }

    @RequestMapping(value = "picturesPreview", method = RequestMethod.POST)
    public String picturesPreview(Model model, HttpServletRequest req) throws UnsupportedEncodingException {
        String urls = req.getParameter("urls");
        String currentUrl = req.getParameter("currentUrl");
        // 路径转码
        String decodedUrl = URLDecoder.decode(urls, "utf-8");
        String decodedCurrentUrl = URLDecoder.decode(currentUrl, "utf-8");
        // 抽取文件并返回文件列表
        String[] imgs = decodedUrl.split("\\|");
        List imgurls = Arrays.asList(imgs);
        model.addAttribute("imgurls", imgurls);
        model.addAttribute("currentUrl",decodedCurrentUrl);
        return "picture";
    }
    /**
     * 根据url获取文件内容
     * 当pdfjs读取存在跨域问题的文件时将通过此接口读取
     *
     * @param urlPath
     * @param resp
     */
    @RequestMapping(value = "/getCorsFile", method = RequestMethod.GET)
    public void getCorsFile(String urlPath, HttpServletResponse resp) {
        InputStream inputStream = null;
        try {
            String strUrl = urlPath.trim();
            URL url = new URL(new URI(strUrl).toASCIIString());
            //打开请求连接
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = httpURLConnection.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            while (-1 != (len = inputStream.read(bs))) {
                resp.getOutputStream().write(bs, 0, len);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    /**
     * 通过api接口入队
     * @param url 请编码后在入队
     */
    @GetMapping("/addTask")
    @ResponseBody
    public String addQueueTask(String url) {
        cacheService.addQueueTask(url);
        return "success";
    }

    public static void main(String[] args) {
        String url = "http://localhost:8012/demo/557422 精通Spring 4.x 企业应用开发实战[www.rejoiceblog.com].pdf";

        url = url.substring(url.indexOf("demo"));
        System.out.println(url);

        StringBuffer sb = new StringBuffer();
        String cardId = "420117199305284714";
        sb.append(cardId.substring(0,6));
        sb.append("********");
        sb.append(cardId.substring(14));

        System.out.println(sb.toString());

    }

}
