
package com.fastdfs.test;

import java.net.InetSocketAddress;

import com.fastdfs.client.ClientGlobal;
import com.fastdfs.client.StorageClient1;
import com.fastdfs.client.StorageServer;
import com.fastdfs.client.TrackerClient;
import com.fastdfs.client.TrackerGroup;
import com.fastdfs.client.TrackerServer;
import com.fastdfs.common.NameValuePair;

public class Test1 {
	public static void main(String args[]) {
		try {
			ClientGlobal.init("d:\\fastdfs_client.conf");
			System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
			System.out.println("charset=" + ClientGlobal.g_charset);

			TrackerGroup tg = new TrackerGroup(new InetSocketAddress[] { new InetSocketAddress("10.0.0.121", 22122) });
			TrackerClient tc = new TrackerClient(tg);

			TrackerServer ts = tc.getConnection();
			if (ts == null) {
				System.out.println("getConnection return null");
				return;
			}

			StorageServer ss = tc.getStoreStorage(ts);
			if (ss == null) {
				System.out.println("getStoreStorage return null");
			}

			StorageClient1 sc1 = new StorageClient1(ts, ss);

			NameValuePair[] meta_list = null; // new NameValuePair[0];
			String item = "c:/windows/system32/notepad.exe";
			String fileid = sc1.upload_file1(item, "exe", meta_list); // �����쳣

			System.out.println("Upload local file " + item + " ok, fileid=" + fileid);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
