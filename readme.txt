本来打算写一堆英文(拼音),考虑到读者英语(拼音)水平,思来想去还是中文吧
第一步:搭建fastdfs平台,自己百度一下地址
第二步:下载我的这个jar包,比官方的好用点,修改过一些代码
第三步:
配置fastdfs信息:
文件名固定:fastdfs_client.conf
放到class目录
示例如下具体参数意思就不解释了,相信你一看就知道,真不知道的话就自己猜或问我
connect_timeout = 2
network_timeout = 30
charset = utf-8
http.access.url=http://www.baidu.com/
tracker_server = 192.168.7.61:22122
tracker_server = 192.168.7.62:22122
调用
com.fastdfs.common.FastDFSUtils工具类的uploadFile方法,返回的地址就是fastdfs存储的地址,返回为null就是失败了