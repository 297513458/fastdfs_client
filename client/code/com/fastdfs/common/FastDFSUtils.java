package com.fastdfs.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fastdfs.client.ClientGlobal;
import com.fastdfs.client.StorageClient1;
import com.fastdfs.client.StorageServer;
import com.fastdfs.client.TrackerClient;
import com.fastdfs.client.TrackerGroup;
import com.fastdfs.client.TrackerServer;
import com.fastdfs.client.UploadStream;

public class FastDFSUtils {
	protected static final Logger logger = LogManager.getLogger(FastDFSUtils.class);

	public static void init() {
		if (ClientGlobal.g_tracker_group == null) {
			InputStream is = null;
			if (is == null) {
				try {
					is = FastDFSUtils.class.getClassLoader().getResourceAsStream("/fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					is = FastDFSUtils.class.getClassLoader().getResourceAsStream("fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					is = Thread.currentThread().getClass().getResourceAsStream("fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					is = Thread.currentThread().getContextClassLoader().getResourceAsStream("../fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					String path = FastDFSUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					if (path.lastIndexOf(File.separator) >= 0)
						path = path.substring(0, path.lastIndexOf(File.separator));
					else if (path.lastIndexOf("/") >= 0)
						path = path.substring(0, path.lastIndexOf("/"));
					is = new FileInputStream(path + "/fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					String path = FastDFSUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					if (path.lastIndexOf(File.separator) >= 0)
						path = path.substring(0, path.lastIndexOf(File.separator));
					else if (path.lastIndexOf("/") >= 0)
						path = path.substring(0, path.lastIndexOf("/"));
					is = new FileInputStream(path + "/../fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is == null) {
				try {
					String path = FastDFSUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					if (path.lastIndexOf(File.separator) >= 0)
						path = path.substring(0, path.lastIndexOf(File.separator));
					else if (path.lastIndexOf("/") >= 0)
						path = path.substring(0, path.lastIndexOf("/"));
					is = new FileInputStream(path + "/../res/fastdfs_client.conf");
				} catch (Exception e) {
					logger.warn("查找fdfs_client.conf" + e.getMessage());
				}
			}
			if (is != null)
				try {
					ClientGlobal.init(is);
				} catch (Exception e) {
					logger.warn("请配置正确的fdfs_client.conf");
					throw new RuntimeException("请配置正确的fastdfs_client.conf");
				}
			else
				throw new RuntimeException("请配置fastdfs_client.conf");
		}
		logger.info("=======================fastdfs已经初始化======================");
		logger.info("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
		logger.info("charset=" + ClientGlobal.g_charset);
	}

	/**
	 * 发送文件到fastdfs
	 * 
	 * @param file
	 *            完整的文件路径
	 * @return 文件路径,须加域名或ip才能访问(配置http.access.url时会加上该配置项)
	 * @throws Exception
	 */
	public static String uploadFile(String file) throws Exception {
		try {
			return uploadFile(file, null);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 发送文件到fastdfs
	 * 
	 * @param is
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @return 文件路径,须加域名或ip才能访问(配置http.access.url时会加上该配置项)
	 * @throws Exception
	 */
	public static String uploadFile(InputStream is, String fileName) throws Exception {
		StorageServer ss = null;
		TrackerServer ts = null;
		try {
			if (is == null || fileName == null || fileName.trim().length() <= 0)
				return null;
			logger.info("start upload file " + fileName);
			init();
			TrackerGroup tg = ClientGlobal.g_tracker_group;
			TrackerClient tc = new TrackerClient(tg);
			ts = tc.getConnection();
			if (ts == null) {
				logger.warn("upload " + fileName + " error,getConnection return null");
				throw new RuntimeException("getConnection return null");
			}
			ss = tc.getStoreStorage(ts);
			if (ss == null) {
				logger.warn("upload " + fileName + " error,getStoreStorage return null");
				throw new RuntimeException("getStoreStorage return null");
			}
			StorageClient1 sc1 = new StorageClient1(ts, ss);
			String ext = null;
			if (fileName.lastIndexOf(".") > 0 && (fileName.lastIndexOf(".") + 1 < fileName.length()))
				ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			NameValuePair[] meta_list = null;
			String fileid = sc1.upload_file1(null, is.available(), new UploadStream(is, is.available()), ext,
					meta_list); //
			logger.info("upload file " + fileName + " ok, fileid=" + fileid);
			if (fileid != null && ClientGlobal.httpAccessUrl != null && ClientGlobal.httpAccessUrl.startsWith("http"))
				return ClientGlobal.httpAccessUrl + fileid;
			else
				return fileid;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				ss.close();
			} catch (Exception e) {
			}
			try {
				ts.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 发送文件到fastdfs
	 * 
	 * @param file
	 *            完整的文件路径
	 * @return 文件路径,须加域名或ip才能访问(配置http.access.url时会加上该配置项)
	 * @throws Exception
	 */
	public static String uploadFile(String file, String fileName) throws Exception {
		StorageServer ss = null;
		TrackerServer ts = null;
		try {
			if (file == null || file.trim().length() <= 0)
				return null;
			logger.info("start upload file " + file);
			init();
			TrackerGroup tg = ClientGlobal.g_tracker_group;
			TrackerClient tc = new TrackerClient(tg);
			ts = tc.getConnection();
			if (ts == null) {
				logger.warn("upload " + file + " error,getConnection return null");
				throw new RuntimeException("getConnection return null");
			}
			ss = tc.getStoreStorage(ts);
			if (ss == null) {
				logger.warn("upload " + file + " error,getStoreStorage return null");
				throw new RuntimeException("getStoreStorage return null");
			}
			StorageClient1 sc1 = new StorageClient1(ts, ss);
			String ext = null;
			if (file.lastIndexOf(".") > 0 && (file.lastIndexOf(".") + 1 < file.length()))
				ext = file.substring(file.lastIndexOf(".") + 1, file.length());
			if (fileName != null && fileName.lastIndexOf(".") > 0
					&& (fileName.lastIndexOf(".") + 1 < fileName.length()))
				ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			NameValuePair[] meta_list = null;
			String fileid = sc1.upload_file1(file, ext, meta_list); //
			logger.info("upload file " + file + " ok, fileid=" + fileid);
			if (fileid != null && ClientGlobal.httpAccessUrl != null && ClientGlobal.httpAccessUrl.startsWith("http"))
				return ClientGlobal.httpAccessUrl + fileid;
			else
				return fileid;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				ss.close();
			} catch (Exception e) {
			}
			try {
				ts.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 发送文件到fastdfs
	 * 
	 * @param bytes
	 *            字节流
	 * @param fileName
	 *            文件名
	 * @return 文件路径,须加域名或ip才能访问(配置http.access.url时会加上该配置项)
	 * @throws Exception
	 */
	public static String uploadFile(byte[] bytes, String fileName) throws Exception {
		StorageServer ss = null;
		TrackerServer ts = null;
		try {
			if (fileName == null || fileName.trim().length() <= 0)
				return null;
			logger.info("start upload file " + fileName);
			init();
			TrackerGroup tg = ClientGlobal.g_tracker_group;
			TrackerClient tc = new TrackerClient(tg);
			ts = tc.getConnection();
			if (ts == null) {
				logger.warn("upload " + fileName + " error,getConnection return null");
				throw new RuntimeException("getConnection return null");
			}
			ss = tc.getStoreStorage(ts);
			if (ss == null) {
				logger.warn("upload " + fileName + " error,getStoreStorage return null");
				throw new RuntimeException("getStoreStorage return null");
			}
			StorageClient1 sc1 = new StorageClient1(ts, ss);
			String ext = null;
			if (fileName.lastIndexOf(".") > 0 && (fileName.lastIndexOf(".") + 1 < fileName.length()))
				ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			NameValuePair[] meta_list = null;
			String fileid = sc1.upload_file1(bytes, ext, meta_list); //
			logger.info("upload file " + fileName + " ok, fileid=" + fileid);
			if (fileid != null && ClientGlobal.httpAccessUrl != null && ClientGlobal.httpAccessUrl.startsWith("http"))
				return ClientGlobal.httpAccessUrl + fileid;
			else
				return fileid;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				ss.close();
			} catch (Exception e) {
			}
			try {
				ts.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 删除文件,删除文件不建议使用
	 * 
	 * @param file
	 *            文件路径,不包括域名或ip部分(路径上包含配置http.access.url时会去掉该配置项)
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static boolean deleteFile(String file) throws Exception {
		StorageServer ss = null;
		TrackerServer ts = null;
		try {
			if (file == null || file.trim().length() <= 0)
				return false;
			logger.info("start delete file " + file);
			init();
			TrackerGroup tg = ClientGlobal.g_tracker_group;
			TrackerClient tc = new TrackerClient(tg);
			ts = tc.getConnection();
			if (ts == null) {
				logger.warn("upload " + file + " error,getConnection return null");
				throw new RuntimeException("getConnection return null");
			}
			ss = tc.getStoreStorage(ts);
			if (ss == null) {
				logger.warn("upload " + file + " error,getStoreStorage return null");
				throw new RuntimeException("getStoreStorage return null");
			}
			StorageClient1 sc1 = new StorageClient1(ts, ss);
			if (ClientGlobal.httpAccessUrl != null && file.startsWith(ClientGlobal.httpAccessUrl))
				file = file.replace(ClientGlobal.httpAccessUrl, "");
			int result = sc1.delete_file1(file);
			logger.info("deleted file " + file + " ok, result=" + result);
			return result > 0;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				ss.close();
			} catch (Exception e) {
			}
			try {
				ts.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int t = 0;
		while (true)
			try {
				System.err.println(FastDFSUtils.uploadFile("d:\\QQ截图20160314142403.png"));
				if (t == 9999)
					Thread.sleep(3000000);
			} catch (Exception e) {
			}
	}
}