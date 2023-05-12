package cn.chenpeiyu.bilihub.service;
import cn.chenpeiyu.bilihub.dao.FileDao;
import cn.chenpeiyu.bilihub.domain.File;
import cn.chenpeiyu.bilihub.util.FastDFSUtil;
import cn.chenpeiyu.bilihub.util.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class FileService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    public String uploadFileBySlices(MultipartFile slice,
                                         String fileMD5,
                                         Integer sliceNo,
                                         Integer totalSliceNo) throws Exception {
//      实现秒传功能 逻辑: 上传文件的同时会根据md5 将文件的地址也放到数据库当中去
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
//      如果之前上传过相关的数据,就直接返回地址
        if(dbFileMD5 != null){
            return dbFileMD5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        if(!StringUtil.isNullOrEmpty(url)){
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
//            将文件路径地址传到字符串当中去
            fileDao.addFile(dbFileMD5);
        }
        return url;
    }


    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }

    public File getFileByMd5(String fileMd5) {
        return fileDao.getFileByMD5(fileMd5);
    }
}
