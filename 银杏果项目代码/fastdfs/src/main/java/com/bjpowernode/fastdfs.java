package com.bjpowernode;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.List;

public class fastdfs {

    // 封装获取StorageClient的方法
    public static StorageClient getStorageClient() throws IOException, MyException {

         TrackerServer trackerServer = null;
         StorageServer storageServer = null;
        //1.加载配置文件，默认去classpath下加载
        ClientGlobal.init("fdfs_client.conf");
        //2.创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //3.创建TrackerServer对象
        trackerServer = trackerClient.getConnection();
        //4.创建StorageServler对象
        storageServer = trackerClient.getStoreStorage(trackerServer);
        //5.创建StorageClient对象，这个对象完成对文件的操作
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        return storageClient;
    }


    public static void closeFastDFS() {
         TrackerServer trackerServer = null;
         StorageServer storageServer = null;

        if (storageServer != null) {
            try {
                storageServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (trackerServer != null) {
            try {
                trackerServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 上传文件的方法
    public static String fileUpload(MultipartFile file) {
        try {
            //1. 获取StorageClient对象
            StorageClient storageClient = getStorageClient();

            //2.上传文件
            // 第一个参数：文件字节数组
            // 第二个参数：上传文件的后缀
            // 第三个参数：文件信息
            String[] uploadArray = storageClient.upload_file(file.getBytes(), getFileExtension(file.getOriginalFilename()), null);

            // 检查上传是否成功
            if (uploadArray != null && uploadArray.length > 0) {
                // 返回文件访问路径，例如：http://<你的FastDFS tracker地址>/<groupName>/<remoteFileName>
                return "http://192.168.21.139:22122/" + uploadArray[0] + "/" + uploadArray[1];
            } else {
                return "文件上传失败";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败";
        } catch (MyException e) {
            e.printStackTrace();
            return "文件上传失败";
        } finally {
            closeFastDFS();
        }
    }

    // 获取文件扩展名
    private static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastIndex = fileName.lastIndexOf('.');
        return lastIndex != -1 ? fileName.substring(lastIndex + 1) : null;
    }



    public static void main(String[] args) throws IOException {
        // 获取实际文件路径
        String filePath = "C:\\Users\\LENOVO\\Desktop\\新建 文本文档.txt"; // 替换为您的实际文件路径
        File file = new File(filePath);

        // 使用实际文件创建 MultipartFile 对象
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", Files.readAllBytes(file.toPath()));

        // 上传文件
        String fileUrl = fileUpload(multipartFile);
        System.out.println("文件访问路径：" + fileUrl);
    }
}

