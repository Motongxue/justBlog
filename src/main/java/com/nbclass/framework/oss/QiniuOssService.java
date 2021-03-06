package com.nbclass.framework.oss;

import com.nbclass.framework.exception.OssException;
import com.nbclass.framework.util.GsonUtil;
import com.nbclass.model.BlogFile;
import com.nbclass.vo.ConfigStorageVo;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Region;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云存储
 */
public class QiniuOssService extends OssService {
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private String token;

    QiniuOssService(ConfigStorageVo config){
        this.config = config;

        //初始化
        init();
    }

    private void init(){
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
        uploadManager = new UploadManager(cfg);
        bucketManager = new BucketManager(auth, cfg);
        token = auth.uploadToken(config.getQiniuBucketName());
    }

    @Override
    public BlogFile upload(byte[] data, String path, boolean isPublic) {
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
            DefaultPutRet putRet = GsonUtil.fromJson(res.bodyString(), DefaultPutRet.class);
            BlogFile blogFile = new BlogFile();
            blogFile.withFilePath(path)
                    .withFileFullPath(config.getQiniuDomain() + "/" + path)
                    .withFileName(getFileName(path))
                    .withFileType(getFileType(path))
                    .withFileHash(putRet.hash)
                    .withOssType(OssTypeEnum.QINIU.getValue());
            return blogFile;
        } catch (Exception e) {
            throw new OssException("上传文件失败，请核对七牛配置信息", e);
        }
    }

    @Override
    public BlogFile upload(InputStream inputStream, String path, boolean isPublic) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path, isPublic);
        } catch (IOException e) {
            throw new OssException("上传文件失败", e);
        }
    }

    @Override
    public BlogFile uploadSuffix(byte[] data, String suffix, boolean isPublic) {
        return upload(data, getPath(config.getQiniuPrefix(), suffix), isPublic);
    }

    @Override
    public BlogFile uploadSuffix(InputStream inputStream, String suffix, boolean isPublic) {
        return upload(inputStream, getPath(config.getQiniuPrefix(), suffix), isPublic);
    }

    @Override
    public void delete(String path) {
        try {
            bucketManager.delete(config.getQiniuBucketName(),path);
        } catch (QiniuException e) {
            throw new OssException("删除文件失败", e);
        }
    }
}
