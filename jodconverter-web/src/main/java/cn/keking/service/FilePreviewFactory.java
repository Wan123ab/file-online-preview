package cn.keking.service;

import cn.keking.model.FileAttribute;
import cn.keking.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * Created by kl on 2018/1/17.
 * Content : 文件预览工厂，根据文件属性找到合适的文件预览处理器FilePreview
 */
@Service
public class FilePreviewFactory {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ApplicationContext context;

    /**
     * 也可以通过这种方式注入容器中所有指定类型的bean，Key为beanName，value为bean
     */
    @Autowired
    private Map<String, FilePreview> filePreviewMap;

    public FilePreview get(String url) {
        /**
         * 获取容器中所有指定类型的bean，Key为beanName，value为bean
         */
//        Map<String, FilePreview> filePreviewMap = context.getBeansOfType(FilePreview.class);
        FileAttribute fileAttribute = fileUtils.getFileAttribute(url);
        return filePreviewMap.get(fileAttribute.getType().getInstanceName());
    }
}
